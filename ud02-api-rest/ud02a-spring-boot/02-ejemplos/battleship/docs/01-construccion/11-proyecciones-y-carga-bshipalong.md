# Proyecciones y carga controlada en `bshipAlong`

Esta guía continúa [el contrato HTTP paginado](10-contrato-http-paginado-bshipalong.md)
y toma como referencia conceptual [proyecciones en JPA](../../../../01-documentacion/04-persistencia/10-proyecciones-jpa.md). La paginación de la guía anterior debe
estar terminada y verificada de forma independiente antes de comenzar este corte.

> **Punto de control previo:** aplica esta lección solo después de comprobar
> independientemente 13d. Las comprobaciones docentes en copia no sustituyen la
> ejecución de las pruebas en tu propio proyecto.

## Objetivo y límites

Una entidad JPA representa el modelo de persistencia. Un DTO representa el
contrato que queremos entregar o recibir. Una **proyección** permite pedir a
JPA únicamente las columnas necesarias para una lectura concreta y construir
directamente un objeto de lectura.

En este corte añadiremos una lectura resumida de partidas. No cambiaremos el
endpoint existente, su firma ni su respuesta HTTP: seguirá devolviendo el
contrato anterior. Tampoco usaremos `fetch join` para «arreglar» una consulta
que no hemos medido.

La conversión actual de una entidad a respuesta consulta explícitamente barcos y
ataques por partida. Una asociación `LAZY` no significa que esas colecciones se
carguen automáticamente en la misma consulta; se inicializan cuando el código
las necesita dentro de una sesión adecuada. Por eso separamos la consulta
resumida y medimos antes de afirmar nada sobre rendimiento.

## 1. Crear el DTO de la proyección

En los ejemplos de esta lección se usa el paquete de aula `es.daw2a.bshipalong`. Crea
`src/main/java/es/daw2a/bshipalong/dto/GameSummaryProjection.java`:

```java
package es.daw2a.bshipalong.dto;

import es.daw2a.bshipalong.domain.GameStatus;

import java.time.LocalDateTime;

public record GameSummaryProjection(
        Long id,
        int boardSize,
        GameStatus status,
        LocalDateTime createdAt
) {
}
```

El constructor canónico del `record` coincide con el constructor que usará la
consulta JPQL. No añadimos barcos ni ataques: no forman parte del resumen.

## 2. Añadir dos consultas explícitas al repositorio

En `src/main/java/es/daw2a/bshipalong/repository/GameRepository.java`,
conserva los métodos existentes y añade los imports y métodos siguientes:

```java
import es.daw2a.bshipalong.domain.GameStatus;
import es.daw2a.bshipalong.dto.GameSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
```

Dentro de la interfaz:

```java
@Query(value = """
        select new es.daw2a.bshipalong.dto.GameSummaryProjection(
            g.id, g.boardSize, g.status, g.createdAt
        )
        from Game g
        where g.active = true
        """,
        countQuery = """
        select count(g)
        from Game g
        where g.active = true
        """)
Page<GameSummaryProjection> findActiveSummaries(Pageable pageable);

@Query(value = """
        select new es.daw2a.bshipalong.dto.GameSummaryProjection(
            g.id, g.boardSize, g.status, g.createdAt
        )
        from Game g
        where g.active = true
          and g.status = :status
        """,
        countQuery = """
        select count(g)
        from Game g
        where g.active = true
          and g.status = :status
        """)
Page<GameSummaryProjection> findActiveSummariesByStatus(
        @Param("status") GameStatus status,
        Pageable pageable);
```

Las dos consultas son deliberadamente distintas: una no tiene estado opcional y
la otra exige un estado. `Pageable` limita y ordena la consulta de contenido;
`countQuery` cuenta exactamente el mismo conjunto filtrado. La proyección usa
la clase completa porque JPQL necesita resolver el constructor sin depender de
imports.

No sustituyas aquí las consultas que usa el endpoint: la respuesta HTTP
paginada que construimos en la guía 10 permanece sin cambios.

## 3. Prerrequisito: aislamiento de la prueba

Antes de crear la prueba, confirma que el perfil/configuración de tests del
proyecto selecciona H2 y que la clase se ejecutará con transacciones revertidas.
No cambies perfiles ni fuerces propiedades desde esta lección: el estudiante
debe elegir la configuración H2 existente para pruebas y evitar una base
PostgreSQL compartida. Sin este prerrequisito, una consulta sin datos propios
podría observar filas ajenas y la independencia no estaría garantizada.

## 4. Prueba aislada y determinista

Crea `src/test/java/es/daw2a/bshipalong/repository/GameSummaryQueriesTest.java`
con el siguiente archivo completo:

```java
package es.daw2a.bshipalong.repository;

import es.daw2a.bshipalong.domain.Game;
import es.daw2a.bshipalong.domain.GameStatus;
import es.daw2a.bshipalong.dto.GameSummaryProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GameSummaryQueriesTest {

    @Autowired
    private GameRepository games;

    @Test
    void firstPageContainsOnlyActiveSummaryFields() {
        Game first = save(8, GameStatus.PENDING, true, 1);
        save(9, GameStatus.IN_PROGRESS, true, 2);
        save(10, GameStatus.CANCELLED, false, 3);

        Page<GameSummaryProjection> page = games.findActiveSummaries(
                PageRequest.of(0, 1, Sort.by("id").ascending()));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isZero();
        assertThat(page.getContent()).singleElement().satisfies(summary -> {
            assertThat(summary.id()).isEqualTo(first.getId());
            assertThat(summary.boardSize()).isEqualTo(8);
            assertThat(summary.status()).isEqualTo(GameStatus.PENDING);
            assertThat(summary.createdAt()).isEqualTo(first.getCreatedAt());
        });
    }

    @Test
    void secondPageHasRemainingActiveGame() {
        save(8, GameStatus.PENDING, true, 1);
        Game second = save(9, GameStatus.IN_PROGRESS, true, 2);

        Page<GameSummaryProjection> page = games.findActiveSummaries(
                PageRequest.of(1, 1, Sort.by("id").ascending()));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getContent()).extracting(GameSummaryProjection::id)
                .containsExactly(second.getId());
    }

    @Test
    void pageBeyondLastHasNoContent() {
        save(8, GameStatus.PENDING, true, 1);

        Page<GameSummaryProjection> page = games.findActiveSummaries(
                PageRequest.of(1, 5, Sort.by("id").ascending()));

        assertThat(page).isEmpty();
        assertThat(page.getTotalElements()).isOne();
        assertThat(page.getTotalPages()).isOne();
        assertThat(page.getNumber()).isEqualTo(1);
    }

    @Test
    void cancelledGameIsExcludedByActiveFilter() {
        save(8, GameStatus.PENDING, true, 1);
        Game cancelled = save(9, GameStatus.CANCELLED, false, 2);

        Page<GameSummaryProjection> page = games.findActiveSummaries(
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(page.getContent()).extracting(GameSummaryProjection::id)
                .doesNotContain(cancelled.getId());
    }

    @Test
    void statusFilterKeepsActiveGamesWithRequestedStatus() {
        Game matching = save(8, GameStatus.PENDING, true, 1);
        save(9, GameStatus.IN_PROGRESS, true, 2);
        save(10, GameStatus.PENDING, false, 3);

        Page<GameSummaryProjection> page = games.findActiveSummariesByStatus(
                GameStatus.PENDING,
                PageRequest.of(0, 10, Sort.by("id").ascending()));

        assertThat(page.getContent()).extracting(GameSummaryProjection::id)
                .containsExactly(matching.getId());
        assertThat(page.getTotalElements()).isOne();
        assertThat(page.getTotalPages()).isOne();
    }

    private Game save(int boardSize, GameStatus status, boolean active, int minutes) {
        Game game = new Game(boardSize);
        game.setStatus(status);
        game.setActive(active);
        game.setCreatedAt(LocalDateTime.of(2026, 1, 1, 12, minutes));
        return games.saveAndFlush(game);
    }
}
```

Cada prueba crea sus propios datos, usa identificadores obtenidos de las
entidades guardadas y ordena explícitamente por `id`. La prueba de página vacía
no consulta una base vacía: pide una página posterior a la última de un conjunto
sembrado. Para que el aislamiento sea válido, ejecuta esta clase contra una base
H2 de pruebas aislada y con rollback transaccional; no contra PostgreSQL externo
ni contra datos compartidos. Elige la configuración H2 que ya proporcione el
proyecto para tests, sin forzar aquí una sobreescritura de perfiles. Las fechas
se siembran de forma determinista; `@PrePersist` conserva esos valores y completa
solo los que falten.

Ejecuta únicamente esta prueba desde la raíz de `bshipAlong`:

```bash
./mvnw -Dtest=GameSummaryQueriesTest test
./mvnw test
```

La puerta verde de este corte es que esa prueba pase en la configuración H2 de
tests, una vez cumplido el punto de control de aislamiento. Este documento no
afirma que la prueba esté verde antes de ejecutarla.

## 5. Observar SQL sin prometer cifras

Para inspeccionar las consultas en H2, usa el logging SQL del proyecto y ejecuta
la prueba focalizada anterior. Esa prueba ejecuta **solo la proyección**, no el
listado HTTP completo. Como observación complementaria, ejecuta por separado las
pruebas HTTP de la guía 10 y compara sus logs; no es una medición controlada de
rendimiento porque usan distintos escenarios y datos:

- ¿qué columnas aparecen en el `select`?
- ¿se ejecuta una consulta de contenido y otra de conteo al usar `Page`?
- ¿cuándo aparecen consultas adicionales de barcos o ataques?
- ¿qué cambia al pedir una página vacía?

Esta observación no verifica por sí sola un número universal de consultas ni
una mejora de rendimiento. El número depende del proveedor, configuración,
estado de la sesión y datos. No uses `fetch join` ni cambies HTTP para obtener
una cifra favorable: primero formula una hipótesis, mídela con datos
controlados y documenta el contexto.
