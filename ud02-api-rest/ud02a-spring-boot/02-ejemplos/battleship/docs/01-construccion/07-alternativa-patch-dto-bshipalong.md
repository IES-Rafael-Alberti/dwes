# Alternativa: PATCH con DTO tipado y validación

Esta variante de `bshipAlong` sustituye únicamente el PATCH con `JsonNode` del
guion 11. Se prepara para una rama de demostración: no cambia el recorrido
principal, el PUT, las entidades ni las migraciones.

**Antes de empezar:** crear la rama desde el último checkpoint verde del guion
11. Este documento no crea esa rama ni modifica por sí mismo la aplicación.
Las rutas siguientes son relativas a la raíz de `02-ejemplos/bshipAlong/`.

## 1. Contrato que vamos a enseñar

| Entrada | Resultado |
|---|---|
| Campo ausente o con `null` | Conservar su valor actual |
| Campo con valor válido | Aplicar el valor, incluidos `0` y `false` |
| Ningún valor no nulo (`{}` o todos los campos a `null`) | `400 VALIDATION_ERROR` |
| Nombre vacío, longitud menor que 1 o coordenada negativa | `400 VALIDATION_ERROR` |
| Configuración final fuera del tablero, nombre repetido o solapamiento | `400 BAD_REQUEST` |
| Partida no editable | `409 CONFLICT` |

No se intenta distinguir ausencia de `null`. Es una simplificación deliberada,
no una implementación de JSON Merge Patch ni de JSON Patch. Enviar un valor
igual al actual está permitido: repetir la petición conserva el resultado.

`@Valid` valida el DTO al entrar por HTTP. El servicio valida la configuración
resultante y las reglas que dependen de la partida. Sus tests no deben exigir
que una llamada Java directa active la validación MVC.

**Campos desconocidos y coerción de tipos:** dependen de la configuración de
Jackson. Esta alternativa no promete reproducir el rechazo estricto del árbol
JSON anterior. No cambiar la configuración global del proyecto solo para esta
demostración; revisar esta diferencia al comparar las dos ramas.

## 2. Crear el DTO

**Archivo nuevo:** `src/main/java/es/daw2a/bshipalong/dto/PatchShipDTO.java`.

```java
package es.daw2a.bshipalong.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PatchShipDTO(
        @Size(max = 50)
        @Pattern(regexp = "(?s).*\\S.*", message = "Ship name must not be blank")
        String shipName,
        @Min(1) Integer length,
        @Min(0) Integer startX,
        @Min(0) Integer startY,
        Boolean isHorizontal
) {
    @AssertTrue(message = "At least one field must be provided")
    public boolean isUpdateProvided() {
        return shipName != null || length != null || startX != null
                || startY != null || isHorizontal != null;
    }
}
```

Los wrappers permiten representar «sin cambio». `@Min`, `@Size` y `@Pattern`
aceptan `null`, pero validan los valores no nulos. El patrón exige al menos un
carácter que no sea un espacio en blanco; `(?s)` permite también saltos de línea.
No se usa `@NotNull` en cada propiedad porque obligaría a enviar todos los campos.
`@AssertTrue` exige que haya al menos un valor y se prueba desde la capa web.

## 3. Cambiar el servicio, sin tocar PUT ni las reglas compartidas

**Archivo:** `src/main/java/es/daw2a/bshipalong/service/GameService.java`.

Añadir a los imports:

```java
import es.daw2a.bshipalong.dto.PatchShipDTO;
```

Sustituir **todo el método `patchShip` existente**, no añadir una sobrecarga:

```java
public GameResponseDTO patchShip(Long gameId, Long shipId, PatchShipDTO dto) {
    Game game = findGame(gameId);
    Ship ship = findShip(gameId, shipId);
    ensureShipEditable(game);

    String name = dto.shipName() != null ? dto.shipName() : ship.getShipName();
    int length = dto.length() != null ? dto.length() : ship.getLength();
    int startX = dto.startX() != null ? dto.startX() : ship.getStartX();
    int startY = dto.startY() != null ? dto.startY() : ship.getStartY();
    boolean horizontal = dto.isHorizontal() != null
            ? dto.isHorizontal() : ship.isHorizontal();

    validateShipConfiguration(game, shipId, name, length, startX, startY, horizontal);

    ship.setShipName(name);
    ship.setLength(length);
    ship.setStartX(startX);
    ship.setStartY(startY);
    ship.setHorizontal(horizontal);
    shipRepository.save(ship);
    return toResponse(game);
}
```

Se calculan los valores candidatos y se validan **antes** de modificar la entidad.
Mantener `findShip`, `ensureShipEditable`, `validateShipConfiguration`, `isCurrent`,
`occupies` y `toResponse`, además de la transacción existente de `GameService`.

Retirar `requireText`, `requireInt` y `requireBoolean` si no tienen más usos.
Retirar el import de `JsonNode` del servicio si ya no se utiliza. No retirar
`PlaceShipDTO`: sigue siendo necesario para crear y sustituir barcos con PUT.

## 4. Cambiar el controlador

**Archivo:** `src/main/java/es/daw2a/bshipalong/web/GameController.java`.

Añadir el import del DTO y conservar el de `@Valid` que ya utiliza PUT:

```java
import es.daw2a.bshipalong.dto.PatchShipDTO;
import jakarta.validation.Valid;
```

Sustituir **solo el método PATCH**, incluida su anotación:

```java
@PatchMapping("/{id}/ships/{shipId}")
public ResponseEntity<GameResponseDTO> patchShip(
        @PathVariable("id") Long id,
        @PathVariable("shipId") Long shipId,
        @Valid @RequestBody PatchShipDTO dto) {
    return ResponseEntity.ok(service.patchShip(id, shipId, dto));
}
```

Retirar el import de `JsonNode` si no quedan otros usos en el controlador.
El advice existente ya traduce `MethodArgumentNotValidException` a
`400 VALIDATION_ERROR`; no hay que añadir un segundo handler.

**Todavía no ejecutar Maven:** faltan por adaptar los tests que llaman a la firma
antigua. Maven compila todos los tests aunque se seleccione una sola clase.

## 5. Adaptar los tests sin mezclar responsabilidades

Los tests de servicio pasan ahora `PatchShipDTO`, no árboles JSON. Los casos que
exigen validación del DTO se trasladan al slice web; las reglas del estado
combinado siguen probándose en el servicio. Las peticiones HTTP conservan JSON:
cambia el tipo Java que recibe el controlador, no el formato de la petición.

### 5.1. Tests de servicio: sustituir las entradas JSON

**Archivo:** `src/test/java/es/daw2a/bshipalong/service/ShipUpdateRulesTest.java`.

Añadir:

```java
import es.daw2a.bshipalong.dto.PatchShipDTO;
```

Retirar los imports de `JsonNode` y `ObjectMapper`, el campo
`@Autowired ObjectMapper mapper` y el helper `json(String text)`.
Conservar los helpers `game`, `place` y `ship`, las anotaciones de la clase y
todos los tests de PUT, pertenencia, estado y geometría existentes.

Sustituir estos dos métodos completos:

```java
@Test void patchChangesOnlyProvidedFields() {
    Long gameId = game();
    Long shipId = place(gameId, "A", 2, 0, 0, true);

    var result = service.patchShip(gameId, shipId,
            new PatchShipDTO("B", null, null, null, null));

    var updated = ship(result, shipId);
    assertThat(updated.shipName()).isEqualTo("B");
    assertThat(updated.length()).isEqualTo(2);
    assertThat(updated.startX()).isZero();
    assertThat(updated.startY()).isZero();
    assertThat(updated.isHorizontal()).isTrue();
    assertThat(ships.findById(shipId).orElseThrow().getShipName()).isEqualTo("B");
}

@Test void patchIsIdempotent() {
    Long gameId = game();
    Long shipId = place(gameId, "A", 2, 0, 0, true);
    var request = new PatchShipDTO("B", null, null, null, null);

    service.patchShip(gameId, shipId, request);
    var result = service.patchShip(gameId, shipId, request);

    assertThat(ship(result, shipId).shipName()).isEqualTo("B");
    assertThat(ships.findByGame_Id(gameId)).hasSize(1);
}
```

En `rejectsEditAfterFirstAttack`, conservar el caso de PUT y sustituir solo la
aserción de PATCH por:

```java
assertThatThrownBy(() -> service.patchShip(gameId, shipId,
        new PatchShipDTO(null, null, null, 1, null)))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot edit ships after the first attack");
```

Eliminar **solo** estos dos métodos antiguos, incluidas sus anotaciones `@Test`:

- `patchRejectsNullUnknownAndEmptyFields`: la forma de la petición pasa a la web;
  además, `null` ya no significa error si hay otro valor que aplicar.
- `rejectsOutOfRangeIntegerWithoutChangingShip`: un `Integer` no puede contener
  ese número; el rechazo debe comprobarse en la deserialización HTTP. Se conserva
  `outOfRangePatchReturns400AndKeepsShip` en `ShipUpdateHttpTest`.

Añadir estos casos nuevos **dentro de `ShipUpdateRulesTest`**, fuera de los demás
métodos. No necesitan imports adicionales a los anteriores:

```java
@Test void patchAppliesZeroAndFalse() {
    Long gameId = game();
    Long shipId = place(gameId, "A", 2, 2, 2, true);

    var result = service.patchShip(gameId, shipId,
            new PatchShipDTO(null, null, 0, null, false));

    var updated = ship(result, shipId);
    assertThat(updated.shipName()).isEqualTo("A");
    assertThat(updated.length()).isEqualTo(2);
    assertThat(updated.startX()).isZero();
    assertThat(updated.startY()).isEqualTo(2);
    assertThat(updated.isHorizontal()).isFalse();
}

@Test void patchValidatesMergedConfigurationBeforeChangingShip() {
    Long gameId = game();
    Long shipId = place(gameId, "A", 2, 0, 0, true);

    // 7 es una coordenada no negativa, pero un barco de longitud 2 no cabe ahí.
    assertThatThrownBy(() -> service.patchShip(gameId, shipId,
            new PatchShipDTO("B", null, 7, null, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Ship does not fit horizontally");

    var stored = ships.findById(shipId).orElseThrow();
    assertThat(stored.getShipName()).isEqualTo("A");
    assertThat(stored.getStartX()).isZero();
}

@Test void patchRejectsDuplicateNameAndOverlap() {
    Long gameId = game();
    Long shipId = place(gameId, "A", 2, 0, 0, true);
    place(gameId, "B", 2, 4, 4, true);

    assertThatThrownBy(() -> service.patchShip(gameId, shipId,
            new PatchShipDTO("B", null, null, null, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Ship name already used");
    assertThatThrownBy(() -> service.patchShip(gameId, shipId,
            new PatchShipDTO(null, null, 4, 4, null)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Ships cannot overlap");
}
```

La última prueba no hace una segunda operación válida tras el rechazo: se limita
a comprobar reglas del dominio. Las pruebas de integración existentes usan
rollback al finalizar; no confundir eso con observar un commit desde otra conexión.

### 5.2. Slice web: adaptar mocks y probar `@Valid`

**Cambio de clase y capa:** abrir
`src/test/java/es/daw2a/bshipalong/web/ShipUpdateWebTest.java`.

Conservar `@WebMvcTest(GameController.class)`, `@Autowired MockMvc mvc`,
`@MockitoBean GameService service`, el helper `game()` y los tests existentes.
Sustituir el import de `JsonNode` por el del DTO y añadir los estáticos que falten:

```java
import es.daw2a.bshipalong.dto.PatchShipDTO;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
```

En `patchShipReturnsOk` y `editAfterAttackReturns409`, sustituir
`any(JsonNode.class)` por `any(PatchShipDTO.class)`. Sus peticiones JSON y
expectativas de `200` y `409` se conservan. No cambiar los mocks de PUT.

Añadir los siguientes métodos dentro de la clase. Los imports existentes de
`@Test`, `when`, `patch`, `status` y `jsonPath` siguen utilizándose:

```java
@Test void patchPreservesNullAsNoChangeInRequestDto() throws Exception {
    var request = new PatchShipDTO(null, 3, null, null, null);
    when(service.patchShip(1L, 2L, request)).thenReturn(game());

    mvc.perform(patch("/api/games/1/ships/2")
                    .contentType("application/json")
                    .content("{\"shipName\":null,\"length\":3}"))
            .andExpect(status().isOk());

    verify(service).patchShip(1L, 2L, request);
}

@Test void patchWithoutAnyValueReturnsValidationError() throws Exception {
    for (String body : List.of(
            "{}",
            "{\"length\":null}",
            "{\"shipName\":null,\"length\":null,\"startX\":null,"
                    + "\"startY\":null,\"isHorizontal\":null}")) {
        mvc.perform(patch("/api/games/1/ships/2")
                        .contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
    verifyNoInteractions(service);
}

@Test void patchInvalidValuesReturnValidationError() throws Exception {
    for (String body : List.of(
            "{\"shipName\":\"\"}", "{\"shipName\":\"   \"}",
            "{\"shipName\":\"" + "A".repeat(51) + "\"}",
            "{\"length\":0}", "{\"startX\":-1}", "{\"startY\":-1}")) {
        mvc.perform(patch("/api/games/1/ships/2")
                        .contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
    verifyNoInteractions(service);
}

@Test void patchMalformedOrOutOfRangeInputReturnsBadRequest() throws Exception {
    for (String body : List.of(
            "{", "{\"length\":\"tres\"}", "{\"startX\":4294967296}")) {
        mvc.perform(patch("/api/games/1/ships/2")
                        .contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }
    verifyNoInteractions(service);
}
```

Estos tests comprueban que un DTO inválido no alcanza el servicio. No fijan el
texto exacto del primer error porque pueden incumplirse varias restricciones.
El caso de `null` más longitud solo demuestra el enlace HTTP al DTO; la
conservación efectiva del nombre se verifica con la integración siguiente.

### 5.3. Integración HTTP: conservar los flujos y demostrar el contrato nuevo

**Archivo:** `src/test/java/es/daw2a/bshipalong/web/ShipUpdateHttpTest.java`.

Conservar íntegros `putAndPatchThroughHttp`,
`editAfterAttackReturns409ThroughHttp` y
`outOfRangePatchReturns400AndKeepsShip`. No necesitan DTOs ni mocks: entran por
HTTP y ejecutan servicio y persistencia reales.

Dentro de esa misma clase, añadir este test. Reutiliza sus helpers `create`,
`place` y `shipId`, y sus imports de `patch`, `get`, `status`, `jsonPath` y `@Test`:

```java
@Test void typedPatchPreservesNullAndRejectsEmptyWithoutChangingShip() throws Exception {
    String game = create();
    place(game, "A", 2, 2, 2, true);
    Long shipId = shipId(game);
    String url = game + "/ships/" + shipId;

    mvc.perform(patch(url).contentType("application/json")
                    .content("{\"shipName\":null,\"startX\":0,\"isHorizontal\":false}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ships[0].shipName").value("A"))
            .andExpect(jsonPath("$.ships[0].length").value(2))
            .andExpect(jsonPath("$.ships[0].startX").value(0))
            .andExpect(jsonPath("$.ships[0].startY").value(2))
            .andExpect(jsonPath("$.ships[0].isHorizontal").value(false));

    mvc.perform(patch(url).contentType("application/json").content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));

    mvc.perform(get(game)).andExpect(status().isOk())
            .andExpect(jsonPath("$.ships[0].shipName").value("A"))
            .andExpect(jsonPath("$.ships[0].startX").value(0))
            .andExpect(jsonPath("$.ships[0].isHorizontal").value(false));
}
```

## 6. Comprobaciones, ahora sí ejecutables

**Verificación de esta alternativa — 12 de noviembre de 2026:** aplicada en una
copia temporal del proyecto, sin modificar `bshipAlong` ni crear una rama.
Los diez métodos de test reproducidos en este documento se contrastaron con los
de la copia. El control dirigido pasó con 22 tests y la suite completa, tras
aplicar literalmente esos métodos, terminó con:

```text
Tests run: 71, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Ese recuento corresponde a la copia del proyecto de ese día; no es un objetivo
para ajustar eliminando o añadiendo pruebas sin necesidad. Repetir los controles
al aplicar la alternativa en la rama de clase.

Antes de ejecutar, buscar usos de `patchShip(` en producción y tests. Todos
deben aceptar el DTO nuevo, sin conservar llamadas con `json(...)` ni mocks de
`JsonNode`. No retirar Jackson del POM: Spring MVC lo sigue usando internamente.

Desde la raíz del proyecto:

```bash
./mvnw -Dtest=ShipUpdateRulesTest,ShipUpdateWebTest,ShipUpdateHttpTest test
./mvnw test
```

No ajustar un recuento borrando pruebas ajenas a la adaptación. Comparar los
resultados con el checkpoint inicial y comprobar que los tests de PUT siguen
verdes. La transición de firma es una adaptación coordinada: los fallos de
compilación mientras se actualizan los clientes no se presentan como rojo TDD.

### Qué comparar al mostrar las ramas

- Mismo resultado para un cambio parcial válido; menos código de lectura manual.
- Tipos y restricciones del DTO probados en la entrada HTTP.
- Reglas del juego probadas en el servicio sobre la configuración combinada.
- Diferencia deliberada: `null` significa «conservar», no error por sí mismo.
- No equivale a rechazar cambios desconocidos o coerciones con la misma precisión
  que el ejemplo anterior: esas políticas requieren una decisión adicional.
- El ejemplo es una alternativa de contrato e implementación, no «la única forma
  real» de usar PATCH ni un cambio en la semántica del PUT existente.
