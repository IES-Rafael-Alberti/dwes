# Sesión 6 — Test slicing: probar por capas

## Antes de empezar

Lee el documento [`06-tdd-slicing.md`](../../../01-documentacion/06-tdd-slicing.md). Cubre:

- `@WebMvcTest` — solo capa web
- `@DataJpaTest` — solo repositorios
- `@SpringBootTest` — contexto completo
- Mockito: `@MockitoBean`, `@MockitoSpyBean`

## El problema

Hasta ahora usamos `@SpringBootTest` para tests de servicio y `@WebMvcTest` para controladores. Pero:

- `@SpringBootTest` carga **todo** el contexto (controladores, servicios, repositorios, BD, Flyway...). Es lento y frágil.
- Para probar solo un repositorio, no necesitamos ni el controlador ni el servicio.
- Para probar solo el controlador, no necesitamos la BD real.

La solución: **test slicing** — cargar solo la porción del contexto que necesitas.

## Code-along: tres sabores de test

### 1. @WebMvcTest — solo el controlador (más rápido)

Ya lo usamos en la sesión 3. Repasamos por qué funciona:

```java
@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Test
    void getGameReturns200() throws Exception {
        var game = new GameResponseDTO(1L, 10, "PENDING", null, List.of(), List.of());
        when(gameService.getGame(1L)).thenReturn(game);

        mockMvc.perform(get("/api/games/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
```

Señalar:

- Solo carga: `GameController`, `GlobalExceptionHandler`, `MockMvc`, config web
- NO carga: `GameService`, repositorios, BD, Flyway
- `@MockitoBean` — Mockito crea un mock de `GameService`
- En SB4 es `@MockitoBean`, NO `@MockBean` (cambiado en SB3.2+ / SB4)
- El test vuela (~2s vs ~15s de `@SpringBootTest`)

Tiempos típicos en clase:

| Anotación | Tiempo de arranque | Contexto cargado |
|---|---|---|
| `@WebMvcTest` | ~2s | Solo web |
| `@DataJpaTest` | ~4s | JPA + H2 |
| `@SpringBootTest` | ~12-15s | Completo |

### 2. @DataJpaTest — solo el repositorio

Probamos `GameRepository` sin levantar controladores ni servicios:

```java
package com.example.battleship.repository;

import com.example.battleship.domain.Game;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @Test
    void saveAndFindGame() {
        var game = new Game();
        game.setStatus("PENDING");
        var saved = gameRepository.save(game);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStatus()).isEqualTo("PENDING");

        var found = gameRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    void findAllReturnsAllGames() {
        gameRepository.save(createGame("PENDING"));
        gameRepository.save(createGame("IN_PROGRESS"));

        var all = gameRepository.findAll();
        assertThat(all).hasSize(2);
    }

    private Game createGame(String status) {
        var game = new Game();
        game.setStatus(status);
        return game;
    }
}
```

Señalar:

- `@DataJpaTest` configura solo JPA + H2 + repositorios
- Cada test es **transaccional** y hace **rollback** al final (no se ensucian datos)
- Configura `ddl-auto: create-drop` automáticamente (ignora Flyway)
- Si necesitás datos precargados, usá `@Sql` o un `import.sql`

### 3. @SpringBootTest — integración completa

Cuando necesitás probar el flujo completo (controlador → servicio → repositorio → BD):

```java
@SpringBootTest
@AutoConfigureMockMvc
class BattleshipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullGameFlow() throws Exception {
        // Crear partida
        var createResult = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"boardSize": 10}
                        """))
                .andExpect(status().isCreated())
                .andReturn();

        var location = createResult.getResponse().getHeader("Location");

        // Obtenerla
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
```

Señalar:

- Prueba **real**: sin mocks, con H2 y Flyway reales
- `@AutoConfigureMockMvc` — inyecta MockMvc con el contexto completo
- Útil para smoke tests y scenarios críticos
- **No abuses**: son lentos. Usalos solo para validar flujos completos.

### 4. Comparativa: cuándo usar cada uno

| Quiero probar... | Uso | Mockeo |
|---|---|---|
| Controlador (ruta, JSON, códigos HTTP) | `@WebMvcTest` | Servicio con `@MockitoBean` |
| Repositorio (queries JPQL, SQL nativas) | `@DataJpaTest` | Nada (usa H2 real) |
| Servicio con mocking de repositorio | `@SpringBootTest` + `@MockitoBean` en repos | Repositorio |
| Flujo completo real | `@SpringBootTest` + `@AutoConfigureMockMvc` | Nada |
| Validación de DTOs | `@WebMvcTest` o test unitario de `@Valid` | Según el caso |

### 5. Test del servicio con mock del repositorio

A veces querés probar la lógica del servicio sin tocar la BD:

```java
@SpringBootTest
class GameServiceMockTest {

    @MockitoBean
    private GameRepository gameRepository;

    @Autowired
    private GameService gameService;

    @Test
    void getGameReturnsMappedDto() {
        var game = new Game();
        game.setId(1L);
        game.setStatus("PENDING");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(game));

        var result = gameService.getGame(1L);
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo("PENDING");
    }
}
```

## Lo que vimos hoy

| Concepto | Dónde se ve |
|----------|-------------|
| `@WebMvcTest` | Test de controlador con MockMvc |
| `@DataJpaTest` | Test de repositorio con H2 |
| `@SpringBootTest` + `@AutoConfigureMockMvc` | Test de integración |
| `@MockitoBean` | Mockear dependencias (SB4) |
| Cuándo usar cada slice | Tabla de decisión |
| Rollback automático | `@DataJpaTest` reinicia BD |

## Tarea — Mini-tasks tests

En tu proyecto **mini-tasks**:

1. `@DataJpaTest` para `TaskRepository`:
   - Guardar y recuperar tarea
   - Buscar por estado `findByDone(true)`

2. `@WebMvcTest` para `TaskController`:
   - Mockear `TaskService` con `@MockitoBean`
   - Probar GET, POST, y error 404

3. `@SpringBootTest` + `@AutoConfigureMockMvc`:
   - Smoke test: crear tarea y verificarla

Detalles en `../../03-ejercicios/01-mini-tasks/`.
