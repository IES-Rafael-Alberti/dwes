# Task Manager reference project

Canonical Spring Boot 4 / Java 25 / Thymeleaf example for the UD3 secure MVC itinerary.

## Verify

```bash
GRADLE_USER_HOME=/tmp/m2-products sh gradlew clean build
```

## Run the explicit demo profile

The default profile creates no accounts. Supply temporary credentials through environment variables when you want an interactive demonstration:

```bash
DEMO_USERNAME=teacher-demo \
DEMO_PASSWORD='choose-a-local-password' \
SPRING_PROFILES_ACTIVE=demo \
GRADLE_USER_HOME=/tmp/m2-products \
sh gradlew bootRun
```

Open `http://localhost:8080/login`. The initializer is active only under `demo`, stores a BCrypt hash, is idempotent by username, and never prints the password. Startup fails if either variable is absent or blank. The H2 console remains disabled.

Read `../../01-documentacion/SpringMVC-GestorTareas.md` before extending the project.
