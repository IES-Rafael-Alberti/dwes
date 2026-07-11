# Guía: Migraciones con Flyway en Spring Boot

## Qué es y para qué sirve
- **Flyway** gestiona la evolución del esquema de la base de datos mediante migraciones versionadas.
- Beneficios:
  - Historial claro de cambios (V1, V2, …) y trazabilidad de quién/qué cambió el esquema.
  - Arranques consistentes en todos los entornos (dev/test/prod) sin scripts manuales.
  - Evita usar `ddl-auto=create/update` en producción; se valida que el esquema real coincide con el código.
  - Permite rollback controlado (generando migraciones de corrección) y detectar desajustes.

## Conceptos básicos
- Migraciones se nombran `V<versión>__<descripcion>.sql` (ej. `V1__init.sql`) en `src/main/resources/db/migration`.
- Se ejecutan en orden de versión. Una vez aplicada, Flyway la marca en su tabla interna (`flyway_schema_history`).
- Cambios posteriores → nuevas migraciones (`V2__add_rol_usuario.sql`, etc.), nunca edites las ya aplicadas.

## Configuración en Spring Boot (paso a paso)
1) **Dependencia**:
   - Maven:
     ```xml
     <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
     </dependency>
     ```
   - Gradle:
     ```kotlin
     implementation("org.flywaydb:flyway-core")
     ```
   - Usa el driver de tu BD (Postgres/MySQL/etc.) ya presente en el proyecto.

2) **Propiedades recomendadas** (`application.properties`):
   ```properties
   spring.jpa.hibernate.ddl-auto=validate   # evitar que Hibernate cambie el esquema
   spring.flyway.enabled=true               # por defecto true
   spring.flyway.locations=classpath:db/migration
   # Si ya existe esquema en la BD y quieres partir de ahí:
   # spring.flyway.baseline-on-migrate=true
   ```

3) **Primera migración** (`src/main/resources/db/migration/V1__init.sql`):
   - Define tablas/relaciones iniciales (usuarios, roles, eventos, participantes, organizadores).
   - Si ya tienes esquema creado, usa `baseline-on-migrate=true` para marcarlo como base y añade migraciones posteriores para cambios.

4) **Cambios posteriores**:
   - Añade nueva versión: `V2__add_roles_enum.sql`, `V3__add_refresh_tokens.sql`, etc.
   - No modifiques migraciones aplicadas; crea nuevas con los cambios.

5) **Ejecución**:
   - Flyway corre en el arranque de la app. Si falla, la app no levanta (esquema inconsistente).
   - Para probar local, limpia tu BD o usa perfiles con H2 si prefieres.

## Buenas prácticas
- Versiona también los scripts de datos iniciales si son necesarios (`data.sql` puede convivir, pero mejor migraciones específicas).
- Usa `ddl-auto=validate` para detectar desajustes entre entidades y esquema.
- No uses `ddl-auto=update/create` en entornos serios; migra con SQL explícito.
- Comunica al equipo la disciplina: cada cambio de entidad → nueva migración.

## Ejemplo de estructura de migraciones
```
src/main/resources/db/migration/
  V1__init.sql              -- tablas base (usuarios, roles, eventos, etc.)
  V2__add_indices_unicos.sql -- unicidad email/username, etc.
  V3__add_refresh_tokens.sql -- ejemplo si añades refresh tokens
```

## Ejemplo de `V1__init.sql`
```sql
-- Usuarios y roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL,
    rol_id BIGINT NOT NULL REFERENCES roles(id)
);

-- Dominio de eventos
CREATE TABLE organizadores (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL
);

CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP,
    ubicacion VARCHAR(200),
    organizador_id BIGINT NOT NULL REFERENCES organizadores(id),
    CONSTRAINT uq_evento_nombre UNIQUE (nombre)
);

CREATE TABLE participantes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL
);

CREATE TABLE eventos_participantes (
    evento_id BIGINT REFERENCES eventos(id) ON DELETE CASCADE,
    participante_id BIGINT REFERENCES participantes(id) ON DELETE CASCADE,
    PRIMARY KEY (evento_id, participante_id)
);
```
