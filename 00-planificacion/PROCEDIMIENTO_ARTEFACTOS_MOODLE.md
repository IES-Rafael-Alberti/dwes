# Procedimiento de artefactos para Moodle

Los ZIP y PDF de Moodle no se versionan. Las fuentes editables sí permanecen
en el repositorio y los artefactos definitivos se conservan dentro de la
carpeta `moodle/` de la unidad correspondiente.

Para las unidades revisadas: `moodle/PDF/` contiene documentos de lectura,
`moodle/ZIP/` los proyectos editables y `moodle/README.md` indica qué recurso
subir, su fuente y cuándo publicarlo. No se crean paquetes de unidades
pendientes de revisión. Los PDF de apoyo se mantienen además en
`01-documentacion/`; los ZIP se preparan a partir de las fuentes en
`02-ejemplos/` o `03-ejercicios/`.

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

- `ud00-previos/moodle/ZIP/java25-labs-starters.zip`
- `ud00-previos/moodle/ZIP/calc25-starter.zip`
- `ud00-previos/moodle/ZIP/geonotes-java25-starter.zip`
- `ud00-previos/moodle/ZIP/gincana-java25.zip` (selección pública explícita;
  nunca crear este ZIP incluyendo las carpetas locales `Reto01/`-`Reto06/`).

Para evitar incluir compilaciones locales, una regeneración segura usa solo los
archivos versionados del proyecto:

```bash
git -C ud00-previos/03-ejercicios/03-java25-labs ls-files \
  | zip -q /tmp/java25-labs-starters.zip -@
git -C ud00-previos/03-ejercicios/02-calculadora/recursos/calc25 ls-files \
  | zip -q /tmp/calc25-starter.zip -@
git -C ud00-previos/03-ejercicios/01-geonotes/recursos/geonotes-teaching-java25 ls-files \
  | zip -q /tmp/geonotes-java25-starter.zip -@
```

Después se copian los ZIP a `ud00-previos/moodle/ZIP/` y se comprueban
con `unzip -t`.

## PDF

Los PDF se generan desde la documentación Markdown o desde la fuente de la
unidad mediante el flujo habitual de conversión del curso. No se editan a mano
ni se toman como fuente: si cambia el Markdown, se vuelven a generar y se
reemplaza el PDF de la carpeta `moodle/` de esa unidad.

Los PDF Java actuales son:

- `ud00-previos/moodle/PDF/java-para-programadores-kotlin.pdf`
- `ud00-previos/moodle/PDF/faq-repaso-gincana-java25.pdf`
- `ud00-previos/moodle/PDF/08-integracion-java-moderno.pdf`

El PDF global actualizado de UD01 y los PDF breves para la primera sesión se
encuentran en `ud01-introduccion-entorno-servidor/moodle/PDF/`. La guía de
sesión del profesor permanece en `99-profesor/`, también si se genera en PDF.

## Regla de mantenimiento

Al cerrar una revisión, conservar únicamente los artefactos definitivos dentro
de `DWES/<unidad>/moodle/`. Los intermedios deben ir a `/tmp/opencode` u otra
ubicación temporal y eliminarse después. Nunca crear carpetas de salida de
DWES al nivel de `Modulos/`.

## Calificador

El calificador plantilla no es un PDF, ZIP ni CSV que pueda generarse desde
este repositorio. La configuración reproducible está en
`CONFIGURACION_CALIFICADOR_MOODLE_2026_2027.md`; el curso sin alumnado debe
crearse en Moodle, comprobarse con una cuenta de prueba y respaldarse desde la
propia instalación como `.mbz`.
