## 🧪 Práctica: “Hello REST con Spring Boot 4 + GraalVM + Docker”

### 🎯 Objetivos
- Crear una API REST básica con Spring Boot 4.
- Compilarla como imagen nativa con GraalVM.
- Dockerizar y comparar con versión JVM tradicional.
- (Opcional) Repetir en Kotlin para comparar sintaxis y tamaño.

---

### 🧱 Estructura del proyecto

#### 1. Crear proyecto con Spring Boot 4
Usa [Spring Initializr](https://start.spring.io) con:
- **Spring Boot**: `4.0.0` (o snapshot si aún no está estable)
- **Lenguaje**: Java
- **Dependencias**:
  - Spring Web
  - Spring Boot Actuator
  - GraalVM Native Support

#### 2. Código base: `HelloController.java`
```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hola desde Spring Boot nativo!";
    }
}
```

#### 3. Dockerfile para JVM tradicional
```Dockerfile
FROM eclipse-temurin:21-jdk
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 4. Dockerfile para imagen nativa con GraalVM
```Dockerfile
FROM ghcr.io/graalvm/native-image:ol8-java21 as builder
WORKDIR /app
COPY . .
RUN ./mvnw -Pnative native:compile

FROM scratch
COPY --from=builder /app/target/demo .
ENTRYPOINT ["./demo"]
```

---

### 🧪 Comparativa sugerida para clase

| Versión         | Tiempo arranque | Tamaño imagen | Consumo RAM aprox |
|----------------|-----------------|----------------|--------------------|
| JVM tradicional| ~1.5s           | ~120MB         | ~150MB             |
| GraalVM nativa | ~0.05s          | ~40MB          | ~30MB              |

---

### 🧑‍🏫 Actividades propuestas

1. **Compilar ambas versiones** y medir tiempos con `time curl localhost:8080/hello`.
2. **Comparar logs de arranque** y uso de memoria con `docker stats`.
3. **Analizar el impacto de la reflexión** y cómo Spring AOT la evita.
4. (Opcional) Repetir en Kotlin y comparar tamaño del binario.

---

