# Prompt 2 — Consolidación final de inventario con modelo potente

## Objetivo

Genera el **inventario final consolidado** del material docente del módulo **Desarrollo Web en Entorno Servidor (DWES)**.

Debes usar como entrada:

1. El prompt original de inventario:

   `00_prompt_inventario_material_modulo_BASE.md`

2. El inventario bruto generado en la primera pasada:

   `inventario_bruto_dwes.md`

Tu tarea es revisar, depurar, consolidar y redactar el catálogo final en Markdown.

## Rol del modelo

Actúa como arquitecto docente y revisor curricular.

No te limites a copiar el inventario bruto. Debes:

- Detectar inconsistencias.
- Unificar duplicados evidentes.
- Separar material docente principal de material de corrección.
- Mantener las exclusiones indicadas en el prompt original.
- Señalar material obsoleto, dudoso o pendiente de revisión.
- Proponer criterios útiles para una reorganización posterior.

## Entradas obligatorias

Antes de redactar el informe final, revisa:

- Las reglas del prompt original.
- La tabla de candidatos docentes.
- La tabla de prompts o guías de corrección reutilizables.
- Las categorías excluidas.
- Las dudas marcadas con `requires_review: true`.

## Criterios de consolidación

Al consolidar:

- Respeta las rutas relativas.
- No inventes archivos que no aparezcan en el barrido.
- No conviertas entregas de alumnado en material docente.
- No incluyas ZIPs de alumnado ni repositorios de alumnado en el catálogo principal.
- Sí puedes mencionar categorías excluidas en la sección correspondiente.
- Si un material parece antiguo pero útil para evidenciar un resultado normativo, indícalo.
- Si una tecnología parece obsoleta pero pedagógicamente aprovechable, márcalo como pendiente de revisión, no como descarte automático.

## Formato de salida final

Genera un fichero Markdown llamado preferentemente:

`inventario_material_dwes.md`

Debe seguir exactamente esta estructura:

```md
# Inventario del módulo: Desarrollo Web en Entorno Servidor

## 1. Resumen ejecutivo

Incluye una visión general:

- Qué tipo de materiales predominan.
- Qué partes parecen bien cubiertas.
- Qué partes parecen dispersas, duplicadas o antiguas.
- Qué elementos conviene revisar antes del próximo curso.

## 2. Catálogo de materiales docentes

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |

## 3. Materiales de corrección detectados

Incluye sólo prompts, guías o instrucciones reutilizables para corrección.

No incluyas entregas, ZIPs, repositorios de alumnado ni resultados de evaluación.

## 4. Elementos excluidos deliberadamente

Resume por categorías lo detectado pero no inventariado:

- ZIPs de entregas.
- Repositorios de alumnado.
- Descompresiones de tareas.
- Artefactos de corrección.
- Resultados o informes derivados.

No hace falta listar cada archivo individual salvo que sea necesario para justificar una decisión.

## 5. Riesgos, duplicidades y material dudoso

Indica:

- Material potencialmente duplicado.
- Material que parece obsoleto.
- Material sin contexto claro.
- Archivos que requieren revisión humana.

## 6. Recomendaciones para la siguiente fase

Propón criterios para:

- Diseñar una estructura común entre módulos.
- Separar materiales docentes, evaluación, corrección, entregas y archivo histórico.
- Preparar la revisión curricular del curso siguiente.
```

## Reglas importantes

- No reorganices archivos.
- No borres nada.
- No muevas nada.
- No generes scripts de modificación.
- No ocultes incertidumbre: si algo requiere revisión humana, señálalo.
- El resultado debe ser útil para tomar decisiones de reorganización en una fase posterior.
