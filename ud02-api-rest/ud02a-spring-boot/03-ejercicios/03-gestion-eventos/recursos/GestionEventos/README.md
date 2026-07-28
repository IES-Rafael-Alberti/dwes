# GestionEventos

Small Spring Boot project for managing events, organizers, and participants.

## Profiles (dev, test, prod)

This project uses Spring profiles. For a full explanation see:

- Documentación: Perfiles de Spring → `Documentacion/Perfiles_Spring.md`

Quick start:
- Default active profile is set in `src/main/resources/application.properties` via `spring.profiles.active=dev`.
- You can override it at runtime with `SPRING_PROFILES_ACTIVE=test ./gradlew bootRun` or `-Dspring.profiles.active=prod`.
- In tests, the `test` profile is enforced automatically.

Dev goodies:
- Dev profile seeds fake data via `DataInitializer` (Faker), enables H2 console, and uses `create-drop` schema.

## Run

```bash
./gradlew bootRun
# or build and run the jar
./gradlew bootJar && java -jar build/libs/GestionEventos-0.0.1-SNAPSHOT.jar
```
