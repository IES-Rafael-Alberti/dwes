---

## 🧪 Práctica: “Hello REST con Spring Boot 4 + GraalVM + Docker (Kotlin)”
###  Crear proyecto con Spring Boot 4
Usa [Spring Initializr](https://start.spring.io) con:
- **Spring Boot**: `4.0.0` (o snapshot si aún no está estable)
- **Lenguaje**: Kotlin
- **Dependencias**:
  - Spring Web
  - Spring Boot Actuator
  - GraalVM Native Support
  
### 📁 Estructura del proyecto

```
springboot4-kotlin-native/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/example/demo/
│       │       └── HelloController.kt
│       └── resources/
│           └── application.properties
├── Dockerfile.jvm
├── Dockerfile.native
├── docker-compose.yml
├── Makefile
└── build.gradle.kts
```

---

### 🧑‍💻 Código Kotlin: `HelloController.kt`

```kotlin
package com.example.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/hello")
    fun hello(): String = "Hola desde Kotlin nativo con Spring Boot 4!"
}
```

---

### ⚙️ `build.gradle.kts` (extracto relevante)

```kotlin
plugins {
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    id("org.graalvm.buildtools.native") version "0.10.1"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
```

---

### 🐳 `Dockerfile.jvm`

```Dockerfile
FROM eclipse-temurin:21-jdk
COPY build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

### 🐳 `Dockerfile.native`

```Dockerfile
FROM ghcr.io/graalvm/native-image:ol8-java21 as builder
WORKDIR /app
COPY . .
RUN ./gradlew nativeCompile

FROM scratch
COPY --from=builder /app/build/native/nativeCompile/demo .
ENTRYPOINT ["./demo"]
```

---

### 🧩 `docker-compose.yml`

```yaml
version: '3.8'
services:
  kotlin-jvm:
    build:
      context: .
      dockerfile: Dockerfile.jvm
    ports:
      - "8080:8080"

  kotlin-native:
    build:
      context: .
      dockerfile: Dockerfile.native
    ports:
      - "8081:8080"
```

---

### 🛠️ `Makefile`

```makefile
build-jvm:
	./gradlew build

build-native:
	./gradlew nativeCompile

docker-jvm:
	docker build -t kotlin-jvm -f Dockerfile.jvm .

docker-native:
	docker build -t kotlin-native -f Dockerfile.native .

run:
	docker-compose up
```

---

### 🧪 Actividades sugeridas

- Comparar `/hello` en ambos contenedores (`localhost:8080` vs `localhost:8081`)
- Medir tiempos de arranque con `time curl`
- Ver uso de memoria con `docker stats`
- Analizar diferencias de tamaño y rendimiento entre JVM y nativo
- Discutir sintaxis Kotlin vs Java y ventajas en backend

---

Actividades sugeridas para clase
Ejecutar make build-jvm && make docker-jvm

Ejecutar make build-native && make docker-native

Ejecutar make run y comparar /hello en ambos puertos

Medir tiempos con time curl localhost:8080/hello vs 8081

Ver uso de memoria con docker stats

Analizar diferencias de logs, arranque y tamaño de imagen

Perfecto, José. Aquí tienes una **nota clara y lista para incluir en la práctica**, pensada para tus alumnos que usan Windows. Les explica cómo usar `make` según su entorno y les da alternativas si no lo tienen configurado:

---

## 📝 Nota para alumnos que usan Windows

> ⚠️ **IMPORTANTE: Uso de `make` en Windows**
>
> Esta práctica incluye un archivo `Makefile` para automatizar tareas como compilación, construcción de imágenes Docker y ejecución de contenedores.
>
> Si usas Windows, tienes dos opciones para poder ejecutar `make`:
>
> ### ✅ Opción 1: Usar WSL (Windows Subsystem for Linux)
> - Recomendado para desarrollo backend y contenedores.
> - Instala WSL con Ubuntu siguiendo esta guía oficial: [https://learn.microsoft.com/windows/wsl/install](https://learn.microsoft.com/windows/wsl/install)
> - Una vez instalado, abre Ubuntu y ejecuta los comandos desde ahí:
>   ```bash
>   make build-jvm
>   make build-native
>   make run
>   ```
>
> ### ✅ Opción 2: Usar Git Bash
> - Instala [Git para Windows](https://git-scm.com/download/win)
> - Durante la instalación, activa la opción “Git Bash Here” y “Unix tools from Git”.
> - Abre Git Bash en la carpeta del proyecto y ejecuta los comandos `make`.
>
> ### ❌ Alternativa si no puedes usar `make`
> Puedes ejecutar los comandos manualmente desde terminal:
> ```bash
> ./gradlew build
> ./gradlew nativeCompile
> docker build -t kotlin-jvm -f Dockerfile.jvm .
> docker build -t kotlin-native -f Dockerfile.native .
> docker-compose up
> ```

---


