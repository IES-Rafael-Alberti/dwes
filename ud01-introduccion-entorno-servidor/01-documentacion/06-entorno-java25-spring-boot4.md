# Entorno reproducible: Java 25 y Spring Boot 4

UD1 utiliza Java 25 LTS, Spring Boot 4 y Maven Wrapper. El wrapper fija la versión de Maven del proyecto y evita depender de una instalación global concreta. El JDK sí debe estar instalado y activo.

## Comprobación inicial

Ejecuta:

```bash
java -version
git --version
```

La salida de Java debe comenzar por la versión 25. Comprueba también el compilador:

```bash
javac -version
```

Si `java` y `javac` muestran versiones distintas, revisa `JAVA_HOME` y el `PATH` antes de abrir el proyecto.

## IDE

Puedes usar IntelliJ IDEA o Visual Studio Code con soporte para Java. El IDE debe importar el proyecto como Maven y utilizar el JDK 25 del sistema o un JDK 25 configurado explícitamente.

El proyecto no debe depender de acciones exclusivas del IDE. Compilar y probar desde terminal es la comprobación reproducible.

## Maven Wrapper

Desde `02-ejemplos/hello-server/`:

```bash
./mvnw --version
./mvnw test
```

En Windows usa una terminal compatible con el entorno del curso y ejecuta:

```powershell
mvnw.cmd --version
mvnw.cmd test
```

La propiedad `<java.version>25</java.version>` del `pom.xml` fija la versión de compilación. Spring Boot 4 admite Java 25, aunque su mínimo sea inferior; el módulo adopta 25 para mantener un único baseline.

## Arranque

```bash
./mvnw spring-boot:run
```

En otra terminal:

```bash
curl -i http://localhost:8080/
curl -i http://localhost:8080/api/hello
curl -i http://localhost:8080/health
```

Detén la aplicación con `Ctrl+C`.

## Diagnóstico

| Síntoma | Comprobación |
|---|---|
| `UnsupportedClassVersionError` | `java -version` y JDK configurado en el IDE |
| Maven compila con otra versión | `./mvnw --version` y valor de `JAVA_HOME` |
| Puerto 8080 ocupado | Identifica el proceso; no mates procesos desconocidos |
| El wrapper no ejecuta en Linux/macOS | Comprueba el permiso ejecutable de `mvnw` |
| La prueba falla y el IDE no | Ejecuta siempre `./mvnw test` como fuente de verdad |

No se instala una versión global de Spring Boot. Las dependencias y plugins se declaran en `pom.xml` y Maven las resuelve para el proyecto.

## Evidencia

Conserva únicamente:

- versión de Java y Maven Wrapper;
- resultado de las pruebas;
- estados y tipos de contenido de los tres endpoints.

No entregues rutas personales completas, variables de entorno, tokens ni volcados innecesarios del sistema.
