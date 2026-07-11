# Prompt 1 — Primer barrido de inventario con modelo rápido

## Objetivo

Realiza un **primer barrido estructurado** del material docente del módulo **Desarrollo Web en Entorno Servidor (DWES)** ubicado en el directorio:

`../../Unidades/`

Este trabajo NO debe generar todavía el informe final. Su objetivo es producir un inventario bruto, fiable y revisable que después será consolidado por un modelo más potente.

Usa como referencia principal el fichero:

`00_prompt_inventario_material_modulo_BASE.md`

## Rol del modelo

Actúa como asistente de inventario documental docente.

Tu tarea es:

- Recorrer el directorio indicado y sus subdirectorios.
- Identificar materiales docentes útiles.
- Excluir entregas de alumnado, ZIPs de alumnado, repositorios de alumnado, descompresiones y artefactos derivados de corrección.
- Marcar dudas explícitamente en vez de decidir de forma agresiva.
- Generar una salida estructurada que facilite una segunda pasada de revisión y síntesis.

## Alcance de esta primera pasada

Debes catalogar candidatos a material docente como:

- Apuntes.
- Presentaciones.
- Enunciados de prácticas.
- Guiones de laboratorio.
- Rúbricas.
- Prompts docentes o prompts usados para guiar correcciones.
- Datasets preparados para actividades.
- Scripts o notebooks de apoyo docente.
- Proyectos base o plantillas entregadas al alumnado.
- Documentación de uso de herramientas.
- Materiales de evaluación creados por el docente.
- Recursos de planificación o seguimiento docente.

## Exclusiones estrictas

No inventaries como material docente principal:

- ZIPs con tareas entregadas por alumnado.
- Repositorios de alumnado.
- Resultados de descompresión de entregas.
- Informes de corrección generados.
- CSVs de notas.
- Feedback individual.
- Resultados intermedios de evaluación.
- Artefactos derivados de procesos de corrección.

Sí puedes registrar, en una categoría separada, prompts, guías o instrucciones reutilizables para corrección.

## Estrategia de trabajo

Si el árbol de archivos es grande, trabaja por bloques o unidades, por ejemplo:

- `U1-*`
- `U2a-*`
- `U2b-*`
- `U2c-*`
- `U3-*`
- `U4-*`
- `U5-*`
- `U6-*`
- `Java/`
- `Examenes/`
- `ProyectoConjunto/`
- Archivos sueltos en la raíz

No intentes resolver todo semánticamente en una única inferencia si eso reduce la precisión.

## Formato de salida

Genera un fichero de inventario bruto en Markdown llamado preferentemente:

`inventario_bruto_dwes.md`

Usa esta estructura:

```md
# Inventario bruto — Desarrollo Web en Entorno Servidor

## 1. Candidatos a material docente

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción breve | Herramientas detectadas | Estado aparente | requires_review | Observaciones |
| ---- | -------- | ---- | ------------- | ----------------- | ----------------------- | --------------- | --------------- | ------------- |

## 2. Prompts, guías o instrucciones de corrección reutilizables

| Ruta | Elemento | Uso probable | Unidad/Bloque | Observaciones | requires_review |
| ---- | -------- | ------------ | ------------- | ------------- | --------------- |

## 3. Elementos excluidos por categoría

| Categoría | Rutas o patrones detectados | Motivo de exclusión | Observaciones |
| --------- | -------------------------- | ------------------- | ------------- |

## 4. Dudas para la segunda pasada

| Ruta | Elemento | Duda | Por qué requiere revisión |
| ---- | -------- | ---- | ------------------------- |
```

## Criterios para `requires_review`

Marca `requires_review: true` cuando:

- No esté claro si el material es docente o artefacto de corrección.
- El archivo parezca duplicado.
- El contenido parezca obsoleto pero potencialmente útil.
- El nombre del archivo no permita inferir bien su propósito.
- El material pueda pertenecer a otra unidad o módulo.
- La relación con resultados de aprendizaje o criterios de evaluación sea dudosa.

Marca `requires_review: false` sólo cuando la clasificación sea razonablemente clara.

## Reglas importantes

- No reorganices archivos.
- No borres nada.
- No muevas nada.
- No generes todavía el informe final.
- No rellenes con suposiciones fuertes si no hay evidencia suficiente.
- Prefiere una clasificación prudente y revisable.
- Conserva siempre rutas relativas.
