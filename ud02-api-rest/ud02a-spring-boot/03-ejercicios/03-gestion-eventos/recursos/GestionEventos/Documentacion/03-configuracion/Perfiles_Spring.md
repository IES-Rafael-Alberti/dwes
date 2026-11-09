### Perfiles de Spring Boot en GestionEventos

Este proyecto usa perfiles de Spring para aislar la configuración de desarrollo, pruebas y producción.

#### ¿Qué es un perfil?
Un perfil es un conjunto de propiedades que se activan según el entorno. Permite cambiar base de datos, logging, scripts SQL, etc., sin tocar el código.

#### Perfiles disponibles
- dev
  - H2 en memoria, esquema `create-drop` en cada arranque.
  - Carga de `schema.sql`/`data.sql` si existen (`spring.sql.init.mode=always`).
  - Consola H2 activada en `/h2-console`.
  - Se ejecuta el seeder `DataInitializer` con datos falsos (Faker).
  - Archivo: `src/main/resources/application-dev.properties`.
- test
  - H2 en memoria aislada para pruebas.
  - Esquema `create-drop`.
  - Sin carga automática de SQL ni consola H2.
  - Forzado automáticamente en los tests por `src/test/resources/application.properties`.
  - Archivo: `src/main/resources/application-test.properties`.
- prod
  - Pensado para BD real (PostgreSQL/MySQL). No modifica el esquema (`ddl-auto=none`).
  - Sin carga de SQL, sin consola H2, `open-in-view=false`.
  - Archivo: `src/main/resources/application-prod.properties`.

#### Perfil activo por defecto
En `src/main/resources/application.properties` puedes fijar el perfil por defecto:

```properties
spring.profiles.active=dev  # cámbialo a test o prod cuando quieras
```

Este valor puede ser sobrescrito (tiene prioridad):
- Variable de entorno: `SPRING_PROFILES_ACTIVE=prod`
- Parámetro JVM: `-Dspring.profiles.active=test`

En tests, siempre se usa `test` gracias a `src/test/resources/application.properties`:

```properties
spring.profiles.active=test
```

#### Seeder de datos (solo dev)
`DataInitializer` está anotado con `@Profile("dev")`. Solo sembrará datos falsos cuando el perfil activo sea `dev`.

#### Cómo ejecutar con un perfil concreto
- Desde Gradle (dev por defecto si así está en `application.properties`):
  ```bash
  ./gradlew bootRun
  ```
- Forzando otro perfil en tiempo de ejecución:
  ```bash
  ./gradlew bootRun --args='--spring.profiles.active=prod'
  # o con variable de entorno
  SPRING_PROFILES_ACTIVE=test ./gradlew bootRun
  ```
- Con el JAR:
  ```bash
  java -Dspring.profiles.active=dev -jar build/libs/GestionEventos-0.0.1-SNAPSHOT.jar
  ```

#### Notas útiles
- Consola H2 (solo dev): `http://localhost:8080/h2-console`
- Si decides usar `data.sql`/`schema.sql` en dev, ya está habilitado (`spring.sql.init.mode=always`) y el orden de inicialización está asegurado con `spring.jpa.defer-datasource-initialization=true`.

#### Propiedades de seeding (solo dev)
En el perfil `dev` el seeder `DataInitializer` usa propiedades para controlar el volumen de datos falsos:

```properties
# src/main/resources/application-dev.properties
seed.organizadores=8
seed.eventos=16
seed.participantes=40
```

- Puedes ajustar estos valores para generar más/menos datos.
- Los participantes generados tienen contraseñas encriptadas con BCrypt (solo demo).
- Los eventos se crean con `tipo` aleatorio y fechas realistas (`fechaInicio`/`fechaFin`).
