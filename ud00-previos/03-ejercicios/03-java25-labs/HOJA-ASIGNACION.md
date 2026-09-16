# Hoja de asignacion - Java 25

## Datos

- **Unidad:** UD00 - Previos
- **Materia:** Java
- **Entorno:** JDK 25, IntelliJ IDEA y Gradle Wrapper
- **Modalidad:** trabajo individual, salvo indicacion del profesor
- **Calificacion:** actividad de preparacion; no tiene nota independiente

## Objetivo

Resolver una progresion de ejercicios Java, empezando por pequenas funciones
y terminando con proyectos que integran varias ideas. El objetivo no es hacer
mucho codigo, sino aprender a leer un contrato, probarlo, corregir errores y
explicar las decisiones tomadas.

## Orden de trabajo

### 1. Laboratorio de retos cortos

Descarga `java25-labs-starters.zip` y abre en IntelliJ la carpeta que contiene
`settings.gradle`.

| Orden | Reto | Que se practica | Tiempo |
|---:|---|---|---:|
| 1 | J25-01 - Estadisticas seguras | Validacion, bucles, `List` y `record` | 20 min |
| 2 | J25-03 - Record de temperatura | Constructor compacto y metodos derivados | 25 min |
| 3 | J25-04 - Ultimos titulos | Colecciones, `Comparator` y Streams | 30 min |
| 4 | J25-06 - Fichero de notas | `Path`, `Files`, UTF-8 y errores | 45 min |
| 5 | J25-05 - Resultados sellados | `sealed`, `switch` y patrones | 35 min |
| 6 | J25-08 - Consulta geografica | Records, `record patterns` y filtrado | 60 min |

Ejecuta todos los tests del reto desde IntelliJ o con:

```bash
./gradlew :J25-01:test
```

Sustituye `J25-01` por el reto que estés realizando.

### 2. Proyecto Calc25

Descarga `calc25-starter.zip` cuando hayas terminado los retos iniciales.
Anade el historial de operaciones siguiendo `J25-07/README.md` y conserva los
tests que ya trae el proyecto.

### 3. Proyecto GeoNotes

Descarga `geonotes-java25-starter.zip` como ampliacion final. Trabaja sobre la
hoja de ejercicios incluida en la documentacion de GeoNotes y completa solo
los bloques que indique el profesor.

## Procedimiento para cada reto

1. Descarga y descomprime el ZIP correspondiente.
2. Abre la carpeta raiz en IntelliJ, no una subcarpeta aislada.
3. Lee el README del reto antes de escribir codigo.
4. Ejecuta los tests iniciales y localiza el metodo pendiente.
5. Trabaja durante el tiempo indicado.
6. Si te bloqueas, solicita una pista antes de mirar una solucion.
7. Cuando termine el tiempo, compara con la solucion de referencia si el
   profesor la ha liberado.
8. Ejecuta los tests otra vez y anota una decision o error aprendido.

## Entrega

Cuando se solicite una entrega, sube un ZIP del proyecto sin estas carpetas:

- `build/`
- `.gradle/`
- `.idea/`

El proyecto debe incluir el codigo, los tests y un `README.md` breve con:

- retos realizados;
- comando usado para ejecutar los tests;
- una decision de diseño;
- una dificultad encontrada y como se resolvio.

No subas soluciones de otros ejercicios ni el PDF del libro de referencia.

## Pistas y soluciones

Las pistas se publicaran despues de un primer intento. La solucion de
referencia se podra publicar al acabar el tiempo de clase o tras la sesion de
dudas. Una solucion sirve para comparar y aprender: hay que leerla, ejecutar
los tests y ser capaz de explicar que cambia respecto a la propia version.
