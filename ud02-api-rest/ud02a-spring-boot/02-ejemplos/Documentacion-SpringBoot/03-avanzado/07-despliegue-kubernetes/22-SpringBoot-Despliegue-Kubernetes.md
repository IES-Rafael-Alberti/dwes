# Despliegue de Spring Boot (.jar) con Docker/Podman y Kubernetes

## 0) Objetivo y supuestos

**Objetivo:** empaquetar una app Spring Boot como imagen OCI (Docker/Podman) y desplegarla en:

* Contenedor local (docker/podman)
* Orquestación: Kubernetes (manifests básicos)

**Supuestos mínimos**

* Proyecto Spring Boot genera `app.jar`
* Java 17/21 (ajusta según tu proyecto)
* Recomendado: exponer endpoint de salud con **Spring Boot Actuator**

---

## 1) Preparar el proyecto para contenedores

### 1.1 Puerto y servidor embebido

Spring Boot en `.jar` normalmente escucha en `8080`. Asegura:

* `server.port=8080` (por defecto)
* No dependas de rutas locales del sistema; todo debe ir por **config/env**.

### 1.2 Actuator (muy recomendado)

En `build.gradle` / `pom.xml` añade:

* Gradle:

  * `implementation 'org.springframework.boot:spring-boot-starter-actuator'`

* Maven:

  * `spring-boot-starter-actuator`

Y en `application.properties` (ejemplo mínimo):

```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.probes.enabled=true
```

Esto habilita endpoints útiles para Kubernetes:

* `/actuator/health/liveness`
* `/actuator/health/readiness`

### 1.3 Configuración por variables de entorno

Evita “hardcode”:

* URL DB, usuario, password, nivel log, perfiles, etc.

Ejemplo típico:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/app}
spring.datasource.username=${DB_USER:app}
spring.datasource.password=${DB_PASS:app}
spring.profiles.active=${SPRING_PROFILES_ACTIVE:default}
```

---

## 2) Dockerizar: enfoque recomendado (multi-stage)

### 2.1 Dockerfile (multi-stage, runtime slim, usuario no-root)

Crea `Dockerfile` en la raíz del proyecto:

```dockerfile
# ---- build stage (compila el jar) ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copiamos primero archivos de build para aprovechar cache
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw -q -DskipTests dependency:go-offline

# Copiamos el código y compilamos
COPY src src
RUN ./mvnw -q -DskipTests package

# ---- runtime stage (solo ejecuta) ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Seguridad: usuario no-root
RUN useradd -r -u 10001 spring
USER spring

# Copiamos el jar final (ajusta el nombre si hace falta)
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080

# Opcional: tuning JVM en contenedores
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

> Si usas **Gradle**, cambia la etapa build a `./gradlew bootJar` y copia el jar desde `build/libs/*.jar`.

---

## 3) Construir y ejecutar con Docker y Podman

### 3.1 Build de imagen

**Docker:**

```bash
docker build -t miapp:1.0 .
```

**Podman:**

```bash
podman build -t miapp:1.0 .
```

### 3.2 Ejecutar

```bash
docker run --rm -p 8080:8080 miapp:1.0
```

Con variables de entorno (ej. DB):

```bash
docker run --rm -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://host.docker.internal:5432/app" \
  -e DB_USER="app" \
  -e DB_PASS="secret" \
  miapp:1.0
```

> En Linux, `host.docker.internal` puede no existir según setup; alternativa: usar red de Docker Compose o una IP/hostname accesible.

---

## 4) Docker Compose (local “tipo preproducción”)

`compose.yaml` ejemplo con PostgreSQL:

```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: app
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    ports:
      - "5432:5432"

  api:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://db:5432/app
      DB_USER: app
      DB_PASS: secret
      SPRING_PROFILES_ACTIVE: docker
    depends_on:
      - db
```

Arranque:

```bash
docker compose up --build
```

---

## 5) Kubernetes: manifiestos mínimos

### 5.1 Conceptos rápidos (lo imprescindible)

* **Deployment**: define réplicas y cómo ejecutar el contenedor.
* **Service**: expone el Deployment dentro del clúster.
* **Ingress** (opcional): expone HTTP desde fuera.
* **ConfigMap/Secret**: configuración y credenciales.
* **Probes**: healthchecks (readiness/liveness).

---

## 6) Manifests Kubernetes (plantilla)

Crea una carpeta `k8s/`.

### 6.1 Namespace (opcional)

`k8s/00-namespace.yaml`

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: miapp
```

### 6.2 ConfigMap (config no sensible)

`k8s/10-configmap.yaml`

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: miapp-config
  namespace: miapp
data:
  SPRING_PROFILES_ACTIVE: "k8s"
  DB_URL: "jdbc:postgresql://postgres.miapp.svc.cluster.local:5432/app"
  DB_USER: "app"
```

### 6.3 Secret (credenciales)

`k8s/11-secret.yaml`

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: miapp-secret
  namespace: miapp
type: Opaque
stringData:
  DB_PASS: "secret"
```

> En real: no hardcodear, usar gestor de secretos (SealedSecrets, External Secrets, Vault…).

### 6.4 Deployment

`k8s/20-deployment.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: miapp
  namespace: miapp
spec:
  replicas: 2
  selector:
    matchLabels:
      app: miapp
  template:
    metadata:
      labels:
        app: miapp
    spec:
      containers:
        - name: miapp
          image: miapp:1.0
          imagePullPolicy: IfNotPresent
          ports:
            - containerPort: 8080
          envFrom:
            - configMapRef:
                name: miapp-config
            - secretRef:
                name: miapp-secret

          # Probes (requieren Actuator + probes.enabled)
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 10

          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 20
            periodSeconds: 20

          resources:
            requests:
              cpu: "100m"
              memory: "256Mi"
            limits:
              cpu: "500m"
              memory: "512Mi"
```

### 6.5 Service (ClusterIP)

`k8s/30-service.yaml`

```yaml
apiVersion: v1
kind: Service
metadata:
  name: miapp
  namespace: miapp
spec:
  selector:
    app: miapp
  ports:
    - name: http
      port: 80
      targetPort: 8080
  type: ClusterIP
```

### 6.6 Ingress (opcional, si hay controlador Ingress)

`k8s/40-ingress.yaml`

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: miapp
  namespace: miapp
spec:
  rules:
    - host: miapp.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: miapp
                port:
                  number: 80
```

---

## 7) Despliegue en Kubernetes (comandos)

### 7.1 Aplicar manifiestos

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/10-configmap.yaml
kubectl apply -f k8s/11-secret.yaml
kubectl apply -f k8s/20-deployment.yaml
kubectl apply -f k8s/30-service.yaml
kubectl apply -f k8s/40-ingress.yaml
```

### 7.2 Comprobar estado

```bash
kubectl -n miapp get pods
kubectl -n miapp get svc
kubectl -n miapp describe deployment miapp
kubectl -n miapp logs deploy/miapp
```

### 7.3 Acceso rápido sin Ingress (port-forward)

```bash
kubectl -n miapp port-forward svc/miapp 8080:80
```

Luego: `http://localhost:8080`

---

## 8) Buenas prácticas mínimas (para nota)

* **Usuario no-root** en la imagen (ya lo hacemos).
* **Variables de entorno** para config, sin secretos en código.
* **Readiness/Liveness probes** (Actuator).
* **resources requests/limits** para scheduling decente.
* **Logs a stdout/stderr** (sin escribir a disco).
* **Graceful shutdown**: Spring Boot lo soporta bien, pero evita operaciones largas sin control.
* **Imagen reproducible**: tag versión (`miapp:1.0`) + CI/CD idealmente.

---

## 9) Alternativa moderna: Buildpacks (sin Dockerfile)

Spring Boot puede generar imágenes OCI “sin Dockerfile” (útil didácticamente para simplificar). En Maven:

```bash
./mvnw -DskipTests spring-boot:build-image -Dspring-boot.build-image.imageName=miapp:1.0
```

En Gradle:

```bash
./gradlew bootBuildImage --imageName=miapp:1.0
```

---

## 10) Qué deberían entregar los alumnos (propuesta)

1. `Dockerfile` funcional + comandos de build/run (Docker o Podman)
2. `compose.yaml` con DB y app levantando
3. Carpeta `k8s/` con `Deployment`, `Service`, `ConfigMap`, `Secret` (e Ingress si se usa)
4. Evidencia: capturas o logs de:

   * `docker/podman ps`
   * `docker compose up`
   * `kubectl get pods/svc`
   * llamada a `/actuator/health`

---

Perfecto 👍
Te dejo **las secciones listas para insertar tal cual** en el **documento único**, **después** de las partes de **Docker** y **Docker Compose**, en formato **nota aclaratoria**, sin repetir conceptos ya vistos y sin subir el nivel innecesariamente.

Puedes copiarlas/pegarlas directamente al final del documento.

---

## Nota sobre *healthcheck* en aplicaciones Spring Boot

En algunos apartados anteriores de despliegue se ha mencionado el concepto de *healthcheck* (especialmente en relación con Kubernetes).
En este contexto, se incluye **solo como nota aclaratoria**, ya que **no es obligatorio** para el uso básico de Docker o Docker Compose.

### Qué es un healthcheck en este contexto

Un *healthcheck* es un mecanismo que permite a la plataforma de contenedores saber si una aplicación:

* ha arrancado correctamente
* está lista para recibir peticiones
* sigue funcionando de forma normal

No está pensado para el usuario final, sino para la infraestructura.

---

### Healthcheck de la base de datos

En Docker Compose, PostgreSQL utiliza su propia herramienta nativa:

* `pg_isready`

Este mecanismo:

* **no depende de Spring Boot**
* **no depende de Java**
* indica si PostgreSQL acepta conexiones

Es suficiente para evitar que la aplicación arranque antes de tiempo.

---

### Healthcheck de la aplicación Spring Boot

En aplicaciones Spring Boot, el mecanismo estándar para healthchecks es **Spring Boot Actuator**.

Actuator expone endpoints como:

* `/actuator/health`
* `/actuator/health/liveness`
* `/actuator/health/readiness`

Estos endpoints:

* son opcionales
* son reutilizables en Docker, paneles de hosting o Kubernetes
* no afectan al funcionamiento normal de la API

En este módulo:

* **no es obligatorio implementarlos**
* se mencionan únicamente como buena práctica

---

### Resumen sobre healthchecks

* Docker Compose **puede funcionar sin healthchecks**
* Su uso mejora la robustez, pero no es imprescindible
* Se trata de un concepto transversal, no específico de este módulo

---

## Nota sobre Flyway / Liquibase y el despliegue

Las herramientas de migración de base de datos como **Flyway** o **Liquibase** suelen generar dudas sobre si forman parte del despliegue o del proyecto.

### Aclaración importante

> Flyway y Liquibase **no son herramientas de despliegue**.

Son parte del **desarrollo backend**.

---

### Qué hacen Flyway y Liquibase

* Gestionan la evolución del esquema de la base de datos
* Versionan cambios estructurales
* Se ejecutan **dentro de la aplicación Spring Boot**

Normalmente:

* se configuran en el proyecto
* se lanzan automáticamente al arrancar la aplicación
* no requieren scripts externos

---

### Relación con Docker y Docker Compose

Docker y Docker Compose:

* **no ejecutan migraciones**
* **no conocen Flyway ni Liquibase**
* solo arrancan contenedores

El flujo real es:

```text
Docker arranca contenedores
→ Spring Boot se inicia
→ Flyway/Liquibase (si existen) se ejecutan
→ La base de datos queda preparada
→ La API queda disponible
```

Por tanto:

* la configuración de Flyway/Liquibase pertenece al proyecto
* no al fichero `docker-compose.yml`
* ni al proceso de despliegue

---

### Por qué se mencionan aquí

Se mencionan únicamente para dejar claro que:

* no es necesario ejecutar SQL manualmente
* no es necesario “preparar” la base de datos a mano
* el proyecto puede ser reproducible al arrancar

Pero **su estudio y configuración pertenece al desarrollo de la aplicación**, no al despliegue.

---

## Cierre del bloque de despliegue

Con lo visto en este documento, un proyecto Spring Boot puede:

* ejecutarse en un contenedor único
* conectarse a una base de datos en otro contenedor
* arrancar con un solo comando (`docker compose up`)
* desplegarse en servidores sin Java

Todo ello sin:

* entrar en Kubernetes
* asumir rol DevOps
* añadir tareas adicionales al proyecto

Este nivel es **suficiente, realista y habitual** para un desarrollador backend.

---

Perfecto 👍
Te dejo **las secciones listas para insertar tal cual** en el **documento único**, **después** de las partes de **Docker** y **Docker Compose**, en formato **nota aclaratoria**, sin repetir conceptos ya vistos y sin subir el nivel innecesariamente.

Puedes copiarlas/pegarlas directamente al final del documento.

---

## Nota sobre *healthcheck* en aplicaciones Spring Boot

En algunos apartados anteriores de despliegue se ha mencionado el concepto de *healthcheck* (especialmente en relación con Kubernetes).
En este contexto, se incluye **solo como nota aclaratoria**, ya que **no es obligatorio** para el uso básico de Docker o Docker Compose.

### Qué es un healthcheck en este contexto

Un *healthcheck* es un mecanismo que permite a la plataforma de contenedores saber si una aplicación:

* ha arrancado correctamente
* está lista para recibir peticiones
* sigue funcionando de forma normal

No está pensado para el usuario final, sino para la infraestructura.

---

### Healthcheck de la base de datos

En Docker Compose, PostgreSQL utiliza su propia herramienta nativa:

* `pg_isready`

Este mecanismo:

* **no depende de Spring Boot**
* **no depende de Java**
* indica si PostgreSQL acepta conexiones

Es suficiente para evitar que la aplicación arranque antes de tiempo.

---

### Healthcheck de la aplicación Spring Boot

En aplicaciones Spring Boot, el mecanismo estándar para healthchecks es **Spring Boot Actuator**.

Actuator expone endpoints como:

* `/actuator/health`
* `/actuator/health/liveness`
* `/actuator/health/readiness`

Estos endpoints:

* son opcionales
* son reutilizables en Docker, paneles de hosting o Kubernetes
* no afectan al funcionamiento normal de la API

En este módulo:

* **no es obligatorio implementarlos**
* se mencionan únicamente como buena práctica

---

### Resumen sobre healthchecks

* Docker Compose **puede funcionar sin healthchecks**
* Su uso mejora la robustez, pero no es imprescindible
* Se trata de un concepto transversal, no específico de este módulo

---

## Nota sobre Flyway / Liquibase y el despliegue

Las herramientas de migración de base de datos como **Flyway** o **Liquibase** suelen generar dudas sobre si forman parte del despliegue o del proyecto.

### Aclaración importante

> Flyway y Liquibase **no son herramientas de despliegue**.

Son parte del **desarrollo backend**.

---

### Qué hacen Flyway y Liquibase

* Gestionan la evolución del esquema de la base de datos
* Versionan cambios estructurales
* Se ejecutan **dentro de la aplicación Spring Boot**

Normalmente:

* se configuran en el proyecto
* se lanzan automáticamente al arrancar la aplicación
* no requieren scripts externos

---

### Relación con Docker y Docker Compose

Docker y Docker Compose:

* **no ejecutan migraciones**
* **no conocen Flyway ni Liquibase**
* solo arrancan contenedores

El flujo real es:

```text
Docker arranca contenedores
→ Spring Boot se inicia
→ Flyway/Liquibase (si existen) se ejecutan
→ La base de datos queda preparada
→ La API queda disponible
```

Por tanto:

* la configuración de Flyway/Liquibase pertenece al proyecto
* no al fichero `docker-compose.yml`
* ni al proceso de despliegue

---

### Por qué se mencionan aquí

Se mencionan únicamente para dejar claro que:

* no es necesario ejecutar SQL manualmente
* no es necesario “preparar” la base de datos a mano
* el proyecto puede ser reproducible al arrancar

Pero **su estudio y configuración pertenece al desarrollo de la aplicación**, no al despliegue.

---

## Cierre del bloque de despliegue

Con lo visto en este documento, un proyecto Spring Boot puede:

* ejecutarse en un contenedor único
* conectarse a una base de datos en otro contenedor
* arrancar con un solo comando (`docker compose up`)
* desplegarse en servidores sin Java

Todo ello sin:

* entrar en Kubernetes
* asumir rol DevOps
* añadir tareas adicionales al proyecto

Este nivel es **suficiente, realista y habitual** para un desarrollador backend.

---

Perfecto. Aquí tienes **las dos secciones adicionales**, **listas para pegar** justo a continuación del bloque anterior de Flyway/Liquibase, manteniendo el mismo tono **docente, claro y sin desviar el foco del módulo**.

---

## Flyway vs Liquibase: cuál elegir en este módulo

Flyway y Liquibase resuelven el **mismo problema** (migraciones de base de datos), pero su enfoque es distinto.
En un módulo orientado a **desarrolladores backend**, conviene tener un criterio claro.

### Comparativa resumida

| Aspecto                              | Flyway              | Liquibase                        |
| ------------------------------------ | ------------------- | -------------------------------- |
| Tipo de migraciones                  | SQL versionado      | Declarativas (XML/YAML/JSON/SQL) |
| Curva de aprendizaje                 | Baja                | Media                            |
| Transparencia                        | Muy alta (SQL puro) | Media                            |
| Control fino del SQL                 | Total               | Parcial                          |
| Complejidad                          | Baja                | Mayor                            |
| Uso habitual en equipos pequeños     | Muy común           | Menos común                      |
| Uso en entornos corporativos grandes | Común               | Muy común                        |

---

### Recomendación para este módulo

**Recomendación principal: Flyway**

Motivos:

* **Ya conocéis SQL**
* Las migraciones son **claras y explícitas**
* Es fácil entender *qué cambia* y *cuándo cambia*
* Reduce la carga conceptual
* Encaja muy bien con proyectos pequeños o medianos

Liquibase:

* es perfectamente válido
* puede usarse si ya se conoce
* o si el proyecto lo requiere expresamente

Pero **no se recomienda introducir ambos** en el mismo proyecto.

---

### Mensaje claro 

> En este módulo, Flyway es la opción más sencilla y directa.
> Liquibase es una alternativa válida, pero más compleja.

---

## Relación con JPA / Hibernate (`ddl-auto`)

Cuando se usan herramientas de migración (Flyway o Liquibase), es importante **ajustar correctamente Hibernate** para evitar conflictos.

### El problema habitual

Hibernate puede:

* crear tablas automáticamente
* modificar el esquema
* borrar columnas sin control

Esto **entra en conflicto** con las migraciones versionadas.

---

### Configuración recomendada

Cuando se usa Flyway o Liquibase, se recomienda:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Esto significa:

* Hibernate **no crea ni modifica** el esquema
* Solo **valida** que las entidades coinciden con la base de datos
* Si hay incoherencias, la aplicación **no arranca**

---

### Otras opciones (no recomendadas aquí)

| Valor         | Motivo para no usarlo              |
| ------------- | ---------------------------------- |
| `create`      | Borra y recrea tablas              |
| `create-drop` | Pierde datos al parar              |
| `update`      | Cambios implícitos, no versionados |
| `none`        | No valida coherencia               |

---

### Flujo correcto con migraciones

```text
Flyway / Liquibase aplica cambios
→ Base de datos queda en el estado esperado
→ Hibernate valida el esquema
→ La aplicación arranca
```

Este flujo:

* es predecible
* es reproducible
* es el más habitual en entornos profesionales

---

## Resumen final del bloque

* Flyway y Liquibase son **herramientas del proyecto**, no del despliegue
* En este módulo:

  * **Flyway es la opción recomendada**
  * Liquibase es válida, pero más compleja
* Si se usan migraciones:

  * `ddl-auto=validate` es la opción correcta
* Docker y Docker Compose:

  * no gestionan esquemas
  * solo arrancan el entorno

---


