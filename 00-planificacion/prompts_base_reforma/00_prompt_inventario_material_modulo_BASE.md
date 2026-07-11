# Prompt para inventariar material docente de un módulo

Inventaría el material docente del módulo **Desarrollo Web en Entorno Servidor (DWES)** ubicado en:

`../../Unidades/`

El objetivo NO es reorganizar todavía, sino crear un catálogo fiable de lo que existe para poder, en una fase posterior:

1. Ver qué materiales hay.
2. Diseñar una estructura común para todos los módulos.
3. Reorganizar los materiales en esa nueva estructura.
4. Decidir modificaciones, añadidos, eliminaciones o actualizaciones para el próximo curso.

Módulos previstos:

- Desarrollo Web en Entorno Servidor.
- Programación de Inteligencia Artificial.
- Sistemas de Big Data.

## Alcance del inventario

Recorre el directorio indicado y todos sus subdirectorios.

Cataloga materiales docentes útiles, por ejemplo:

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

## Exclusiones importantes

Si encuentras carpetas de corrección de tareas, entregas de alumnado o evaluación:

- NO inventaries ZIPs con tareas entregadas por alumnado.
- NO inventaries repositorios de alumnado.
- NO inventaries resultados de descompresión de entregas.
- NO inventaries artefactos generados al corregir, como informes, CSVs de notas, feedback individual, resultados intermedios o ficheros derivados.
- SÍ cataloga prompts, guías, instrucciones o documentos usados para orientar la corrección, porque pueden servir para repetir o mejorar el proceso.

Ten en cuenta estas decisiones futuras:

- Los repositorios de alumnado se eliminarán en una fase posterior.
- Los ZIPs de tareas se archivarán y almacenarán.
- Los resultados de descomprimir entregas se eliminarán en una fase posterior.
- Los artefactos de corrección y evaluación se archivarán, pero no forman parte del catálogo docente principal.

## Criterio de clasificación

Para cada elemento inventariado, indica:

- Ruta relativa.
- Nombre del archivo o carpeta.
- Tipo de material.
- Descripción breve.
- Unidad, tema, práctica o bloque al que parece pertenecer.
- Si parece material vigente, obsoleto, duplicado, incompleto o pendiente de revisión.
- Herramientas, tecnologías o plataformas mencionadas.
- Posible relación con resultados de aprendizaje, criterios de evaluación o normativa, si se puede inferir.
- Observaciones útiles para una futura reorganización.

## Consideraciones pedagógicas y curriculares

El catálogo debe servir para una revisión posterior teniendo en cuenta:

- Avances recientes del campo.
- Herramientas actuales frente a herramientas usadas anteriormente.
- Vigencia real de las tecnologías empleadas.
- Coste, gratuidad o viabilidad con bajo presupuesto.
- Disponibilidad de plataformas cloud gratuitas o educativas.
- Normativa vigente del módulo.
- Posibles desajustes entre la normativa y el estado actual de la tecnología.

Cuando una herramienta o tarea parezca antigua pero pueda seguir sirviendo para evaluar un resultado normativo, indícalo. La interpretación puede ser laxa: si una actividad con una herramienta X permite evidenciar un criterio Z aunque la herramienta ya no sea la más usada, debe quedar señalado.

## Formato de salida

Genera un catálogo en Markdown con esta estructura:

```md
# Inventario del módulo: {NOMBRE_MODULO}

## 1. Resumen ejecutivo

Incluye una visión general:

- Qué tipo de materiales predominan.
- Qué partes parecen bien cubiertas.
- Qué partes parecen dispersas, duplicadas o antiguas.
- Qué elementos conviene revisar antes del próximo curso.

## 2. Catálogo de materiales docentes

Tabla con columnas:

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |

## 3. Materiales de corrección detectados

Incluye SOLO prompts, guías o instrucciones reutilizables para corrección.

No incluyas entregas, ZIPs, repositorios de alumnado ni resultados de evaluación.

## 4. Elementos excluidos deliberadamente

Resume por categorías lo que se ha detectado pero NO se ha inventariado:

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

No reorganices archivos todavía. No borres nada. No muevas nada. Sólo inventaría y razona.
```
