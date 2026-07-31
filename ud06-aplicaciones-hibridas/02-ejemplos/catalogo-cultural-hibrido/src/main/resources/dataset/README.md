# Dataset local: fixtura cultural de Wikidata (CC0)

## Propósito

Fixture educativo versionado para el proyecto Catálogo Cultural Híbrido (UD6).
Contiene registros culturales de dominio público usados como fuente local de ingesta.

## Registros

| # | QID / ID | Título | Creador(es) | Año | Verificado |
|---|----------|--------|-------------|-----|------------|
| 1 | Q480 | Don Quixote | Miguel de Cervantes | 1605 | Wikidata vía API |
| 2 | Q12418 | Mona Lisa | Leonardo da Vinci | 1503 | Wikidata vía API |
| 3 | Q252485 | The Great Wave off Kanagawa | Katsushika Hokusai | 1831 | Wikidata vía API |
| 4 | Q208758 | Las Meninas | Diego Velázquez | 1656 | Wikidata vía API |
| 5 | Q41567 | Hamlet | William Shakespeare | 1601 | Wikidata vía API |

Todos los registros usan QIDs canónicos de Wikidata y su URL canónica de entidad
(`https://www.wikidata.org/wiki/<QID>`). Verificados contra la API de Wikidata:
`Las Meninas` es `Q208758` y `Hamlet` es `Q41567`. No se usan QIDs sintéticos ni
URLs de búsqueda como procedencia.

## Formato

JSON array. Cada registro sigue el contrato del modelo normalizado
definido en `01-documentacion/02-contrato-fase-0.md`.

## Licencia

Datos derivados de Wikidata bajo [CC0 1.0 Universal](https://creativecommons.org/publicdomain/zero/1.0/).
Atribución: Wikimedia Foundation — Wikidata.
