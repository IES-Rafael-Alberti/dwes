# Prompt para reorganizar material docente de Desarrollo Web en Entorno Servidor

## Objetivo

Reorganizar el material docente del módulo **Desarrollo Web en Entorno Servidor (DWES)** para preparar una copia limpia del curso próximo.

El directorio actual contiene las unidades del curso actual:

`../../Unidades/`

La reorganización debe estructurar los materiales dentro de este directorio reorganizado:

`./` (directorio raíz de DWES_2026_2027_reorganizado)

## Regla principal de seguridad

NO modificar el material original del curso actual.

Está permitido:

- Crear carpetas nuevas dentro de la estructura reorganizada.
- Copiar archivos desde el árbol actual hacia la nueva estructura.
- Crear documentos índice, README, manifiestos o informes.

Está prohibido:

- Mover archivos originales.
- Borrar archivos originales.
- Renombrar archivos originales.
- Editar archivos originales.
- Descomprimir entregas de alumnado.
- Copiar entregas de alumnado, repositorios de alumnado, feedback individual o notas al nuevo curso.

Si hay duda, copiar menos y dejar constancia en un informe de revisión.

## Entradas que debes usar

Usa como entrada principal:

1. `inventario_material_dwes.md`
2. `inventario_bruto_dwes.md`
3. `00_prompt_inventario_material_modulo_BASE.md`

El inventario final (`inventario_material_dwes.md`) manda sobre el inventario bruto cuando haya conflicto.

## Fase 1 — Proponer estructura común

Antes de copiar nada, propone una estructura común para todas las unidades.

Estructura base recomendada a partir del material actual (U1, U2a, U2b, U2c, U3, U4, U5, U6, Java, Examenes, ProyectoConjunto):

```txt
../
├── README.md
├── 00-planificacion/
├── 00-recursos-comunes/
├── 00-unidad-0-previos/
│   ├── 01-teoria/
│   ├── 02-ejemplos/
│   ├── 03-practicas/
│   └── 05-recursos/
├── ud01-introduccion-entorno-servidor/
│   ├── README.md
│   ├── 01-teoria/
│   ├── 02-ejemplos/
│   ├── 03-practicas/
│   ├── 04-evaluacion/
│   ├── 05-recursos/
│   ├── 90-archivo/
│   └── 99-profesor/
├── ud02-api-rest/
│   ├── ud02a-spring-boot/
│   ├── ud02b-dotnet/
│   └── ud02c-graphql/
├── ud03-mvc-spring-boot/
├── ud04-php/
├── ud05-laravel/
├── ud06-aplicaciones-hibridas/
└── ud07-proyecto-integrador/
```

Puedes mejorar esta estructura si el inventario lo justifica, pero debes explicar por qué. Presta atención especial a:

- **Java**: decidir si va como previos (en `00-unidad-0-previos/`) o como unidad independiente.
- **U2a/b/c**: si conviene mantener separado o unificar en una unidad de API REST con subvariantes tecnológicas.
- **ProyectoConjunto**: si va como unidad final o se distribuye entre unidades.
- **Examenes**: si va a `00-planificacion/` o a una carpeta de evaluación global.

## Criterios de clasificación

Clasifica cada material así:

| Categoría | Destino recomendado |
| --------- | ------------------- |
| Apuntes, teoría, documentos fuente | `01-teoria/` |
| Ejemplos no evaluables, scripts demo, notebooks de apoyo | `02-ejemplos/` |
| Guiones de laboratorio, prácticas de aula, proyectos base | `03-practicas/` |
| Enunciados evaluables, rúbricas, plantillas de entrega | `04-evaluacion/` |
| Datasets, imágenes, requirements, plantillas, packs reutilizables | `05-recursos/` |
| Versiones antiguas, duplicados útiles, material histórico | `90-archivo/` |
| Soluciones, notas internas, guías de profesor, prompts de corrección | `99-profesor/` |
| Recursos transversales del módulo | `00-recursos-comunes/` |
| Planificación, normativa, relación RA/CE, secuencia didáctica | `00-planificacion/` |

## Exclusiones estrictas

No copies al nuevo curso:

- ZIPs de entregas de alumnado.
- Repositorios de alumnado.
- Carpetas de entregas extraídas.
- Feedback individual.
- CSVs de calificaciones.
- Informes privados de corrección.
- Resultados intermedios de evaluación.
- Artefactos derivados de corrección.

Sí puedes copiar a `99-profesor/`:

- Prompts de corrección reutilizables.
- Guías de corrección reutilizables.
- Rúbricas operativas.
- Plantillas de evaluación.

## Fase 2 — Crear plan de reorganización

Antes de ejecutar copias, genera un plan en Markdown:

`PLAN_REORGANIZACION.md`

Debe incluir:

1. Estructura final propuesta.
2. Tabla de mapeo origen → destino.
3. Elementos excluidos y motivo.
4. Elementos dudosos que requieren revisión humana.
5. Duplicados detectados y decisión propuesta.
6. Riesgos antes de ejecutar la copia.

Formato de tabla:

| Origen | Destino | Acción | Motivo | Revisión humana |
| ------ | ------- | ------ | ------ | --------------- |

Acciones posibles:

- `copiar`
- `copiar_como_recurso`
- `copiar_a_profesor`
- `copiar_a_archivo`
- `excluir`
- `revisar_antes_de_copiar`

## Fase 3 — Ejecutar copia segura

Cuando el plan esté claro, crea la estructura y copia archivos.

Reglas:

- Usa `mkdir -p` o equivalente para crear carpetas.
- Usa copia, no movimiento.
- Conserva extensiones.
- Evita sobrescribir archivos sin decidirlo explícitamente.
- Si hay conflicto de nombres, conserva ambos añadiendo sufijo claro, por ejemplo `_v2`, `_html`, `_pdf`, `_historico`.
- Si un archivo parece derivado de otro, prioriza copiar la fuente editable y manda derivados a `90-archivo/` o a una subcarpeta `publicado/` si son útiles.

## Fase 4 — Crear índices

Crea al menos estos documentos en la nueva carpeta:

```txt
./README.md
./PLAN_REORGANIZACION.md
./INFORME_COPIA.md
```

Y un `README.md` por unidad con:

- Propósito de la unidad.
- Orden recomendado de uso.
- Materiales principales.
- Prácticas asociadas.
- Evaluaciones asociadas.
- Pendientes de revisión.

## Fase 5 — Informe final

Genera:

`INFORME_COPIA.md`

Debe incluir:

- Qué se ha copiado.
- Qué se ha excluido.
- Qué queda pendiente de revisión.
- Duplicados o conflictos resueltos.
- Duplicados o conflictos no resueltos.
- Recomendaciones antes de usar el material el curso próximo.

## Principio rector

El objetivo no es dejarlo perfecto de una vez.

El objetivo es crear una **copia limpia, segura y revisable** para el curso 2026/2027, manteniendo intacto el material del curso actual.
