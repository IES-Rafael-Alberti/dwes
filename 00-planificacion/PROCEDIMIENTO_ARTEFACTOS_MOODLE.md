# Procedimiento de artefactos para Moodle

Los ZIP y PDF de Moodle no se versionan. Las fuentes editables sí permanecen
en el repositorio y los artefactos definitivos se conservan dentro de la
carpeta `moodle/` de la unidad correspondiente.

## Ubicación

- UD00: `ud00-previos/moodle/`
- UD01: `ud01-introduccion-entorno-servidor/moodle/`
- Otras unidades: crear `moodle/` dentro de la unidad antes de generar sus
  salidas.

La regla `**/moodle/` del `.gitignore` evita que estos archivos entren en Git,
pero las carpetas son visibles y fáciles de localizar para subirlas a Moodle.

## ZIP de Java UD00

Fuentes:

- `ud00-previos/03-ejercicios/03-java25-labs/`
- `ud00-previos/03-ejercicios/02-calculadora/recursos/calc25/`
- `ud00-previos/03-ejercicios/01-geonotes/recursos/geonotes-teaching-java25/`

Los ZIP se generan excluyendo `build/`, `.gradle/`, `.idea/`, clases compiladas,
soluciones y PDFs. Se comprueba siempre el contenido con `unzip -l` antes de
subirlos.

Los nombres actuales son:

- `ud00-previos/moodle/Java/java25-labs-starters.zip`
- `ud00-previos/moodle/Java/calc25-starter.zip`
- `ud00-previos/moodle/Java/geonotes-java25-starter.zip`

## PDF

Los PDF se generan desde la documentación Markdown o desde la fuente de la
unidad mediante el flujo habitual de conversión del curso. No se editan a mano
ni se toman como fuente: si cambia el Markdown, se vuelven a generar y se
reemplaza el PDF de la carpeta `moodle/` de esa unidad.

## Regla de mantenimiento

Al cerrar una revisión, conservar únicamente los artefactos definitivos dentro
de `DWES/<unidad>/moodle/`. Los intermedios deben ir a `/tmp/opencode` u otra
ubicación temporal y eliminarse después. Nunca crear carpetas de salida de
DWES al nivel de `Modulos/`.
