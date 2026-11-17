# Battleship: PostgreSQL y consultas JPA

Este recorrido continúa el proyecto de aula `bshipAlong` desde la salida de
[integridad y actualización](07-integridad-y-actualizacion-bshipalong.md):
asociaciones, V4, PUT/PATCH y **65 pruebas verdes** en H2. No parte de la rama
alternativa `feat/patch-dto`. Trabaja desde `master` y ejecuta `./mvnw test`
antes de hacer cambios. Si no tienes esa salida, consulta al profesorado.

Si tu repositorio usa las mismas ramas que el proyecto de aula, comprueba el
estado y, **solo si no hay cambios pendientes**, selecciona `master`:

```bash
git status
git switch master
git log -1 --oneline
./mvnw test
```

El punto de partida del aula es `b1a6b9d`. Si `git status` muestra cambios,
no los descartes ni fuerces el cambio de rama: consúltalo antes de continuar.

## 1. Qué vamos a conseguir

Arrancaremos PostgreSQL en un contenedor independiente, conectaremos Spring Boot,
observaremos cómo Flyway aplica las migraciones y comprobaremos que una fila
persiste tras reiniciar. Después añadiremos dos consultas al repositorio. H2
seguirá utilizándose para las pruebas habituales: pasar esos tests no demuestra
que PostgreSQL funcione; por eso añadiremos una comprobación separada.

Necesitas Java 25, Maven Wrapper del proyecto y Docker con Compose funcionando.
Si ya hay un servidor PostgreSQL en el puerto 5432, no lo modifiques: este
recorrido usa el puerto **15432** del equipo. Si también está ocupado, pide al
profesorado un puerto libre y cámbialo tanto en Compose como en `DB_URL`.

## 2. Dependencias: cómo habla la aplicación con PostgreSQL

En `pom.xml`, dentro de `<dependencies>`, añade estas dos dependencias sin borrar
H2 ni `spring-boot-starter-flyway`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

El controlador JDBC permite la conexión; el módulo de Flyway aporta soporte
para ese motor. Mantener Flyway no convierte una base H2 en PostgreSQL: se
aplicarán las migraciones a una base PostgreSQL nueva, sin trasladar datos H2.

Ejecuta `./mvnw test`. Las pruebas anteriores deben seguir verdes en H2.

## 3. Arrancar PostgreSQL con datos persistentes

En la raíz de `bshipAlong`, crea `compose.yaml`:

```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: bshipalong
      POSTGRES_USER: bship
      POSTGRES_PASSWORD: ${DB_PASSWORD:?Define DB_PASSWORD antes de arrancar}
    ports:
      - "127.0.0.1:15432:5432"
    volumes:
      - bshipalong_pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U bship -d bshipalong"]
      interval: 5s
      timeout: 3s
      retries: 10
volumes:
  bshipalong_pgdata:
```

En una terminal Linux establece una contraseña **solo de desarrollo**, que no
sea ninguna de tus contraseñas reales, y arranca la base:

```bash
export DB_PASSWORD='cambia-esta-clave-local'
docker compose up -d
docker compose ps
```

Espera a que el servicio aparezca como `healthy`. `127.0.0.1` limita el puerto
publicado al propio equipo. El volumen conserva los datos al parar o recrear el
contenedor; **no uses `docker compose down -v`**, porque elimina ese volumen y sus
datos. No subas contraseñas reales ni archivos `.env` con valores al repositorio.
La contraseña de PostgreSQL se establece al inicializar un volumen vacío: cambiar
la variable después no cambia automáticamente la contraseña existente.

## 4. Seleccionar PostgreSQL sin perder H2

Crea `src/main/resources/application-postgres.yaml`:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:15432/bshipalong}
    username: ${DB_USER:bship}
    password: ${DB_PASSWORD}
```

El archivo `application.yaml` original conserva H2 como opción predeterminada.
El perfil `postgres` reemplaza sus datos de conexión; siguen activos Flyway y
`ddl-auto: validate`. En la misma terminal donde exportaste la contraseña:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

Observa en el arranque la conexión a PostgreSQL y las migraciones V1–V4. Si la
base estaba vacía, Flyway las aplica en orden; si ya estaban aplicadas y no han
cambiado, valida el historial y no vuelve a ejecutarlas. Una base con tablas
preexistentes requiere estudiar su historial: no uses `baseline` para ocultar un
problema ni borres datos para conseguir que arranque.

La aplicación escucha en `http://localhost:8080`. En otra terminal, crea una
partida de ejemplo:

```bash
curl -i -X POST http://localhost:8080/api/games \
  -H 'Content-Type: application/json' -d '{"boardSize":8}'
curl -s http://localhost:8080/api/games
```

Detén la aplicación con `Ctrl+C`, vuelve a arrancarla con el mismo perfil y
repite la consulta. La partida sigue ahí porque está en PostgreSQL, no en la H2
en memoria. Inspecciona también el historial:

```bash
docker compose exec db psql -U bship -d bshipalong -c 'SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;'
```

Para apagar PostgreSQL sin borrar sus datos usa `docker compose stop`; para
volver a iniciarlo, `docker compose up -d`. Con un volumen propio puedes hacer
más tarde `docker compose down` para retirar los contenedores y la red, sin `-v`.

## 5. Dos consultas en el repositorio

`JpaRepository` ya ofrece operaciones como `findById` y `save`. Una **consulta
derivada** expresa condiciones en el nombre del método. JPQL es otra forma de
escribir consultas usando entidades y atributos Java, no nombres de tablas SQL.
Estos ejemplos solo añaden métodos de repositorio: no crean endpoints ni cambian
el contrato del listado existente.

Sustituye `src/main/java/es/daw2a/bshipalong/repository/GameRepository.java` por:

```java
package es.daw2a.bshipalong.repository;

import es.daw2a.bshipalong.domain.Game;
import es.daw2a.bshipalong.domain.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByActiveTrueAndStatus(GameStatus status);

    @Query("select g from Game g where g.active = true and g.boardSize >= :minSize")
    List<Game> findActiveWithMinimumBoardSize(@Param("minSize") int minSize);
}
```

- `findByActiveTrueAndStatus` busca partidas activas con el estado indicado.
- La consulta JPQL busca partidas activas cuyo tamaño sea al menos `minSize`.
  `Game`, `active` y `boardSize` son entidad y atributos Java; `:minSize` es un
  parámetro, no texto interpolado en la consulta.
- Ninguna consulta promete orden: si importa el orden, hay que indicarlo.

## 6. Comprobar las consultas con H2

Crea `src/test/java/es/daw2a/bshipalong/repository/GameQueriesTest.java`:

```java
package es.daw2a.bshipalong.repository;

import es.daw2a.bshipalong.domain.Game;
import es.daw2a.bshipalong.domain.GameStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GameQueriesTest {
    @Autowired GameRepository games;

    @Test
    void derivedQueryReturnsOnlyActiveGamesWithRequestedStatus() {
        Game pending = games.save(new Game(8));
        Game running = new Game(10);
        running.setStatus(GameStatus.IN_PROGRESS);
        games.save(running);
        Game cancelled = new Game(12);
        cancelled.cancel();
        games.save(cancelled);
        games.flush();

        assertThat(games.findByActiveTrueAndStatus(GameStatus.PENDING))
                .extracting(Game::getId)
                .containsExactly(pending.getId());
    }

    @Test
    void jpqlReturnsOnlyActiveGamesWithMinimumBoardSize() {
        games.save(new Game(8));
        Game large = games.save(new Game(12));
        Game cancelled = new Game(15);
        cancelled.cancel();
        games.save(cancelled);
        games.flush();

        assertThat(games.findActiveWithMinimumBoardSize(10))
                .extracting(Game::getId)
                .containsExactly(large.getId());
    }
}
```

La anotación `@Transactional` revierte los datos de cada prueba. Las pruebas usan
el contexto y el repositorio reales, no un mock; así detectan si JPA interpreta
la consulta. Comprueba primero esa clase y después la suite:

```bash
./mvnw -Dtest=GameQueriesTest test
./mvnw test
```

Resultado esperado desde el checkpoint de 65: **67 pruebas verdes en H2**. Un
contador mayor solo es correcto si el grupo ha añadido otras pruebas; no borres
pruebas para obtener un número concreto.

## 7. Verificar también PostgreSQL

Crea `src/test/java/es/daw2a/bshipalong/repository/PostgresVerificationIT.java`:

```java
package es.daw2a.bshipalong.repository;

import es.daw2a.bshipalong.domain.Game;
import es.daw2a.bshipalong.domain.GameStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("postgres")
@Transactional
class PostgresVerificationIT {
    @Autowired JdbcTemplate jdbc;
    @Autowired GameRepository games;

    @Test
    void migrationsAndQueriesWorkOnPostgres() {
        assertThat(jdbc.queryForObject(
                "select count(*) from flyway_schema_history where success = true",
                Integer.class)).isEqualTo(4);
        Game pending = games.saveAndFlush(new Game(8));
        Game big = games.saveAndFlush(new Game(12));
        Game cancelled = new Game(15);
        cancelled.cancel();
        games.saveAndFlush(cancelled);

        assertThat(games.findByActiveTrueAndStatus(GameStatus.PENDING))
                .extracting(Game::getId).contains(pending.getId(), big.getId())
                .doesNotContain(cancelled.getId());
        assertThat(games.findActiveWithMinimumBoardSize(10))
                .extracting(Game::getId).contains(big.getId())
                .doesNotContain(pending.getId(), cancelled.getId());
    }
}
```

Esta prueba activa el perfil `postgres` y requiere el servidor arrancado y la
variable `DB_PASSWORD` en la terminal. Comprueba el historial V1–V4 y las dos
consultas en PostgreSQL; la transacción revierte los datos de la prueba. Por su
nombre `*IT` no se ejecuta con el patrón habitual de `mvn test`: se lanza de
forma explícita, sin atribuir su resultado a la suite H2.

```bash
./mvnw -Dtest=PostgresVerificationIT test
```

Debe pasar **una prueba en PostgreSQL**. Si la contraseña o el puerto son
incorrectos, corrige la configuración; no desactives Flyway ni uses `create` para
forzar el arranque. Si agregas más migraciones más adelante, ajusta la expectativa
del historial con el nuevo punto de partida.

## 8. Qué conservar y qué continúa

Se han comprobado 67 pruebas de la suite habitual en H2 y una prueba separada en
PostgreSQL. Las migraciones existentes se aplicaron a una base nueva: no se ha
migrado el contenido previo de H2. El listado HTTP sigue usando su implementación
anterior; filtros, paginación y proyecciones se trabajarán en el siguiente tramo.
