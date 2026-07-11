# Despliegue de una aplicación Spring Boot (.jar) en un contenedor único

## 1. Contexto y objetivo

En este módulo **no se trabaja despliegue en profundidad**, ya que existe un módulo específico para ello.
No obstante, muchos proyectos reales se despliegan hoy en día en **hostings que no disponen de Java**, pero **sí permiten ejecutar contenedores Docker**.

**Objetivo de este documento**
Mostrar cómo una aplicación Spring Boot empaquetada como `.jar` puede:

* Ejecutarse en **un único contenedor**
* Desplegarse en **cualquier servidor con Docker**
* No depender de Java, Tomcat ni configuración específica del sistema

Este contenido es **opcional**, pensado para:

* Uso personal
* Proyecto final
* Despliegue real si el alumno lo desea

---

## 2. Idea clave

> No se despliega una aplicación Java, se despliega un contenedor.

El servidor:

* ❌ No necesita Java
* ❌ No necesita Tomcat
* ❌ No conoce Spring Boot
* ✅ Solo ejecuta un contenedor

Dentro del contenedor va todo lo necesario.

---

## 3. Requisitos previos

* Proyecto Spring Boot funcionando
* Empaquetado como **`.jar`**
* Puerto de escucha estándar (`8080`)
* Docker o Podman instalado **solo en la máquina de despliegue**

---

## 4. Construcción del `.jar`

Ejemplo con Maven:

```bash
mvn clean package
```

Resultado esperado:

```text
target/miapp.jar
```

Con Gradle:

```bash
./gradlew bootJar
```

---

## 5. Dockerfile mínimo (recomendado para hosting)

Crear un fichero `Dockerfile` en la raíz del proyecto:

```dockerfile
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Qué hace este Dockerfile

* Usa una imagen con **Java ya instalado**
* Copia el `.jar` dentro del contenedor
* Expone el puerto de la aplicación
* Arranca Spring Boot automáticamente

---

## 6. Construcción de la imagen

Desde el directorio del proyecto:

```bash
docker build -t miapp:latest .
```

Comprobación local:

```bash
docker run -p 8080:8080 miapp:latest
```

La aplicación debe responder en:

```
http://localhost:8080
```

---

## 7. Despliegue en un servidor o hosting con Docker

### Ejecución básica

```bash
docker run -d \
  --name miapp \
  -p 80:8080 \
  miapp:latest
```

* Puerto **80** → puerto público del servidor
* Puerto **8080** → puerto interno de Spring Boot

---

## 8. Configuración mediante variables de entorno

En hosting **no se editan ficheros `.properties`**.
La configuración se pasa mediante variables de entorno.

Ejemplo:

```bash
docker run -d \
  --name miapp \
  -p 80:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host:5432/app \
  -e DB_USER=app \
  -e DB_PASS=secret \
  miapp:latest
```

Spring Boot las recoge automáticamente.

---

## 9. Logs y diagnóstico

Ver logs del contenedor:

```bash
docker logs miapp
```

Esto es compatible con:

* Paneles de hosting
* Portainer
* Coolify
* CapRover
* Plesk con Docker

---

## 10. Ventajas de este enfoque

| Aspecto              | WAR + Tomcat | JAR + Docker |
| -------------------- | ------------ | ------------ |
| Java en el servidor  | Sí           | No           |
| Configuración manual | Alta         | Mínima       |
| Portabilidad         | Baja         | Muy alta     |
| Reproducibilidad     | Media        | Total        |
| Enfoque moderno      | ❌            | ✅            |

---

# Uso de Docker Compose para ejecutar Spring Boot + PostgreSQL

## 1. Por qué usar Docker Compose en este punto

Una vez que la aplicación Spring Boot:

* ya está empaquetada como `.jar`
* ya funciona dentro de un **contenedor único**

es muy habitual que:

* la base de datos vaya en **otro contenedor**
* ambos se arranquen **juntos**
* no se dependa de servicios externos

Docker Compose permite:

* definir varios contenedores
* conectarlos por red automáticamente
* arrancarlos con **un solo comando**

Todo esto **sin entrar en Kubernetes**.

---

## 2. Idea clave

> Cada servicio, un contenedor.
> La aplicación no conoce “Docker”, solo conoce una URL de base de datos.

Docker Compose:

* crea una red interna
* asigna nombres DNS automáticos
* permite que la app se conecte a `postgres:5432`

---

## 3. Requisitos previos

* Imagen Docker de la aplicación ya creada
  (por ejemplo `miapp:latest`)
* Proyecto Spring Boot configurado para usar variables de entorno
* Docker + Docker Compose disponibles

---

## 4. Estructura mínima recomendada

```text
proyecto/
├── Dockerfile
├── docker-compose.yml
└── target/
    └── miapp.jar
```

---

## 5. docker-compose.yml (Spring Boot + PostgreSQL)

Ejemplo **simple y realista**:

```yaml 
version: "3.9"

services:
  postgres:
    image: postgres:16
    container_name: postgres
    environment:
      POSTGRES_DB: app
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  api:
    image: miapp:latest
    container_name: miapp
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_URL: jdbc:postgresql://postgres:5432/app
      DB_USER: app
      DB_PASS: secret

volumes:
  postgres_data:
```

---

## 6. Claves importantes del fichero

### 6.1 Comunicación entre contenedores

```yaml
DB_URL: jdbc:postgresql://postgres:5432/app
```

* `postgres` es el **nombre del servicio**
* Docker Compose crea DNS interno automáticamente
* **No se usa `localhost`**

---

### 6.2 Persistencia de datos

```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data
```

* Los datos **no se pierden** al parar contenedores
* Se mantienen entre reinicios

---

### 6.3 depends_on (orden de arranque)

```yaml
depends_on:
  - postgres
```

* Asegura que PostgreSQL se lanza antes
* No garantiza que esté *lista*, solo arrancada
  (suficiente para este nivel)

---

## 7. Arranque y parada

### Arrancar todo

```bash
docker compose up
```

O en segundo plano:

```bash
docker compose up -d
```

---

### Parar servicios

```bash
docker compose down
```

Con borrado de volúmenes (⚠️ elimina datos):

```bash
docker compose down -v
```

---

## 8. Logs

Ver logs de la aplicación:

```bash
docker compose logs api
```

Ver logs de PostgreSQL:

```bash 
docker compose logs postgres
```

---

## 9. Acceso a la aplicación

* API:

  ```text
  http://localhost:8080
  ```

* PostgreSQL (si se expone):

  ```text
  localhost:5432
  ```

---

## 10. Ventajas frente a usar PostgreSQL externo

| Aspecto       | DB externa  | DB en Compose |
| ------------- | ----------- | ------------- |
| Instalación   | Manual      | Automática    |
| Configuración | Variable    | Reproducible  |
| Entorno       | Dependiente | Aislado       |
| Portabilidad  | Baja        | Alta          |

---

## 11. Relación con el proyecto final

Este enfoque permite que el proyecto final:

* se ejecute en cualquier máquina
* tenga el mismo comportamiento en local y servidor
* no dependa de servicios preinstalados

Todo con:

```bash
docker compose up
```
---

# Docker Compose avanzado (nivel desarrollador)

## Spring Boot + PostgreSQL con perfil `docker` y healthcheck

## 1. Objetivo del documento

Este documento amplía el uso básico de Docker Compose para:

* separar claramente la **configuración de Docker**
* evitar problemas de arranque por **base de datos no disponible**
* mejorar la **robustez** del entorno local o de hosting

Todo ello **sin entrar en Kubernetes** y manteniendo un enfoque de **desarrollador backend**.

---

## 2. Perfil `docker` en Spring Boot

### 2.1 Por qué usar perfiles

Spring Boot permite definir configuraciones distintas según el entorno:

* `default` → desarrollo local
* `test` → tests
* `docker` → ejecución en contenedores
* `prod` → despliegue real

Esto evita:

* condicionales en código
* cambios manuales antes de desplegar

---

### 2.2 application-docker.properties

Crear el fichero:

```text
src/main/resources/application-docker.properties
```

Contenido mínimo:

```properties
# Puerto interno del contenedor
server.port=8080

# Datasource (valores por entorno)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}

# JPA (ejemplo)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Logs
logging.level.root=INFO
```

> Las variables reales se inyectan desde Docker Compose.

---

## 3. Healthcheck de PostgreSQL

### 3.1 Por qué es importante

`depends_on` **solo garantiza orden de arranque**, no que PostgreSQL esté listo.

Sin healthcheck:

* la aplicación puede arrancar
* la base de datos aún no acepta conexiones
* error de arranque intermitente

---

### 3.2 Healthcheck estándar de PostgreSQL

PostgreSQL incluye la utilidad `pg_isready`.

Se puede usar directamente desde Docker Compose.

---

## 4. docker-compose.yml completo (recomendado)

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16
    container_name: postgres
    environment:
      POSTGRES_DB: app
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app -d app"]
      interval: 5s
      timeout: 5s
      retries: 5

  api:
    image: miapp:latest
    container_name: miapp
    depends_on:
      postgres:
        condition: service_healthy
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DB_URL: jdbc:postgresql://postgres:5432/app
      DB_USER: app
      DB_PASS: secret

volumes:
  postgres_data:
```

---

## 5. Puntos clave del fichero

### 5.1 Comunicación entre contenedores

```text
jdbc:postgresql://postgres:5432/app
```

* `postgres` es el nombre del servicio
* Docker Compose crea la red automáticamente
* Nunca se usa `localhost` entre contenedores

---

### 5.2 Arranque seguro

```yaml
depends_on:
  postgres:
    condition: service_healthy
```

* La API **no arranca** hasta que PostgreSQL responde
* Evita errores intermitentes
* Mejora la experiencia del alumno

---

### 5.3 Persistencia real de datos

```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data
```

* Los datos sobreviven a:

  * reinicios
  * `docker compose down`
* Solo se eliminan con `-v`

---

## 6. Arranque del entorno completo

```bash
docker compose up
```

En segundo plano:

```bash
docker compose up -d
```

---

## 7. Comprobaciones útiles

### Estado de los servicios

```bash
docker compose ps
```

### Logs de PostgreSQL

```bash
docker compose logs postgres
```

### Logs de la aplicación

```bash
docker compose logs api
```

---

## 8. Acceso a la aplicación

* API:

  ```text
  http://localhost:8080
  ```

* PostgreSQL:

  ```text
  localhost:5432
  ```

---

## 9. Relación con hosting real

Este mismo `docker-compose.yml` funciona en:

* VPS con Docker
* servidores sin Java
* paneles tipo Portainer / Coolify / CapRover

Sin cambios en el código.

---

## 10. Qué **no** se pretende con este documento

* ❌ No es Kubernetes
* ❌ No es DevOps
* ❌ No es alta disponibilidad
* ❌ No es CI/CD

Es:

* ✅ entorno reproducible
* ✅ profesional
* ✅ realista
* ✅ suficiente para un desarrollador backend

---

## Nota sobre healthcheck en Docker Compose

En apartados anteriores de despliegue se ha mencionado el concepto de *healthcheck* (especialmente en relación con Kubernetes).  
Aquí se incluye **únicamente como nota aclaratoria**, ya que **no es obligatorio** para trabajar con Docker o Docker Compose en este módulo.

### Healthcheck de PostgreSQL

En Docker Compose **sí es recomendable** definir un healthcheck para PostgreSQL.

Motivo:
- PostgreSQL puede tardar unos segundos en aceptar conexiones
- `depends_on` solo garantiza orden de arranque, no disponibilidad real
- Evita errores intermitentes al iniciar la aplicación

Este healthcheck se basa en la herramienta nativa `pg_isready` y **no depende de Spring Boot**.

### Healthcheck de la API (Spring Boot)

La aplicación Spring Boot **no necesita** healthcheck para funcionar correctamente en Docker Compose.

No obstante, si se desea:
- monitorizar el estado de la API
- integrarla con paneles de hosting
- o reutilizar el proyecto en otros entornos

el mecanismo estándar es **Spring Boot Actuator**, que expone endpoints como `/actuator/health`.

En este módulo:
- su uso es **opcional**
- se considera **buena práctica**, no requisito

## Uso de Flyway / Liquibase en el proyecto Spring Boot

Además del despliegue, los proyectos reales necesitan controlar la **evolución del esquema de la base de datos**.  
Para ello se utilizan herramientas de migración como **Flyway** o **Liquibase**.

### Qué problema resuelven

Sin migraciones:
- cada entorno puede tener una base de datos distinta
- es necesario ejecutar SQL manualmente
- aparecen incoherencias entre código y esquema

Con Flyway o Liquibase:
- la estructura de la base de datos está versionada
- los cambios se aplican automáticamente
- la base de datos se adapta al proyecto al arrancar

---

### Qué hace falta para usarlas

- Proyecto Spring Boot con acceso a base de datos
- **Una sola herramienta**:
  - Flyway **o**
  - Liquibase
- Scripts de migración versionados en el proyecto

No requieren:
- Docker
- Docker Compose
- scripts externos
- pasos manuales en el despliegue

---

### Cómo se usan (idea general)

Flyway y Liquibase:
- se configuran dentro del proyecto
- se ejecutan automáticamente al arrancar Spring Boot
- aplican solo los cambios pendientes
- guardan su estado en tablas internas

Docker y Docker Compose **no ejecutan migraciones**.  
Solo arrancan los contenedores.

El flujo real es:

Docker arranca contenedores  
→ Spring Boot se inicia  
→ Flyway/Liquibase se ejecuta  
→ La base de datos queda preparada  
→ La API queda disponible

---

### Flyway vs Liquibase: recomendación para este módulo

Ambas herramientas son válidas, pero su enfoque es distinto:

- **Flyway**
  - Migraciones en SQL
  - Curva de aprendizaje baja
  - Muy transparente
  - Recomendado para este módulo

- **Liquibase**
  - Migraciones declarativas (XML/YAML/JSON)
  - Más potente, pero más complejo
  - Habitual en entornos corporativos grandes

**Recomendación**: usar **Flyway**, salvo que el proyecto requiera Liquibase.

---

### Relación con JPA / Hibernate

Cuando se usan migraciones, Hibernate **no debe modificar el esquema**.

Configuración recomendada:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Esto significa que:

* Flyway o Liquibase se encargan de **crear y modificar** el esquema
* Hibernate **no crea ni altera** tablas automáticamente
* Hibernate solo **valida** que las entidades Java coinciden con la base de datos
* Si hay incoherencias, la aplicación **no arranca**

---

### Flujo correcto usando migraciones

El flujo completo del proyecto queda así:

Docker / Docker Compose arranca contenedores
→ Spring Boot se inicia
→ Flyway o Liquibase ejecuta migraciones pendientes
→ La base de datos queda en el estado esperado
→ Hibernate valida el esquema
→ La aplicación queda disponible

Este enfoque:

* es predecible
* es reproducible
* es el más habitual en entornos profesionales

---

### Resumen de Flyway / Liquibase en este módulo

* Flyway y Liquibase forman parte del **proyecto**, no del despliegue
* En este módulo:

  * **Flyway es la opción recomendada**
  * Liquibase es válida, pero más compleja
* Si se usan migraciones:

  * `ddl-auto=validate` es la opción correcta
* Docker y Docker Compose:

  * no gestionan esquemas
  * solo arrancan el entorno

---


## Cierre del bloque de despliegue

Con lo visto en este documento, un proyecto Spring Boot puede:

- ejecutarse en un contenedor único
- conectarse a una base de datos en otro contenedor
- arrancar con un solo comando (`docker compose up`)
- desplegarse en servidores sin Java

Todo ello sin:
- entrar en Kubernetes
- asumir un rol DevOps
- añadir complejidad innecesaria al proyecto

Este nivel de despliegue es **realista**, **habitual en empresa** y **suficiente para un desarrollador backend**.



