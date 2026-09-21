# Selección de ejercicios Java 25

Este recorrido aprovecha ideas de *Aprende Java con Ejercicios* (2019), pero no
reproduce el libro ni sus soluciones. Los ejercicios se han reducido y
reescrito para el entorno de esta unidad: JDK 25, IntelliJ, Gradle Wrapper y
JUnit 5.

El libro queda como referencia docente y banco de ideas. JSP, NetBeans,
configuraciones antiguas de JDK, JDBC/MySQL como recorrido principal y ejemplos
de sesiones implementados con tecnología Servlet no forman parte de esta ruta.

## Cómo se trabajará

Cada ejercicio tiene tres niveles de ayuda:

1. **Reto:** enunciado y tests iniciales, sin solución visible.
2. **Pistas:** una o dos ayudas breves para desbloquear el siguiente paso.
3. **Solución comentada:** implementación de referencia, tests y explicación de
   las decisiones.

En clase se resolverán pocos ejercicios y con límite de tiempo. El resto queda
como práctica autónoma. Consultar una solución después del límite no es un
fracaso: hay que comparar el diseño, ejecutar los tests y anotar qué se ha
aprendido o cambiado.

## Recorrido de retos cortos

| ID | Ejercicio | Base aprovechada | Java 25 | Tiempo |
|---|---|---|---|---:|
| J25-01 | Estadísticas seguras | Validación, bucles, listas y tests | `record` para el resultado | 20 min |
| J25-03 | Record de temperatura | POO, validación y métodos derivados | `record` y constructor compacto | 25 min |
| J25-04 | Últimos títulos | Colecciones, ordenación y contratos | Streams, `Comparator` y `toList()` | 30 min |
| J25-06 | Fichero de notas | Rutas, texto, errores y persistencia local | `Path`, `Files` y UTF-8 | 45 min |
| J25-05 | Resultados sellados | Jerarquías, estados y excepciones | `sealed`, `record` y `switch` con patrones | 35 min |
| J25-08 | Consulta geográfica de GeoNotes | Records, filtrado y dominio existente | `record patterns` y Streams | 60 min |

J25-01 y J25-03 son la entrada mínima para quien necesite nivelación. J25-04
y J25-06 introducen colecciones y ficheros antes de pasar a J25-05 y J25-08,
que acercan el trabajo al estilo que se usará en Spring Boot.

## Ampliaciones autónomas

| ID | Ampliación | Producto | Tiempo orientativo |
|---|---|---|---:|
| J25-07 | Extender la calculadora | Comando `history`, `record` y regresión | 45-60 min |

J25-07 reutiliza la calculadora existente y se propone después del laboratorio
de retos cortos. No se introducen todavía APIs web ni Spring Boot: UD00 prepara
el lenguaje y las herramientas; el backend se aborda en las unidades
correspondientes.

## Orden de modernización

- Los ejercicios de fundamentos conservan la claridad del enfoque del libro.
- Las clases de datos nuevas usan `record` cuando el modelo es inmutable.
- Las jerarquías cerradas usan `sealed` y `switch` exhaustivo.
- Las colecciones se resuelven primero con un bucle comprensible y después con
  Streams cuando la transformación gana claridad.
- Las fechas usan `java.time`; no se introducen `Date` ni `SimpleDateFormat`.
- El acceso a ficheros usa `Path` y `Files`.
- La persistencia web se verá con Spring Boot, PostgreSQL/JPA y pruebas, no con
  JSP ni con un CRUD JDBC/MySQL aislado.
- Los hilos virtuales quedan como demostración opcional, no como requisito de
  estos ejercicios.

## Relación con el material existente

- `03-ejercicios/02-calculadora/` cubre ya un ejercicio integrador con retos y
  regresión.
- `03-ejercicios/01-geonotes/` cubre records, sealed types, pattern matching,
  text blocks, Streams y colecciones secuenciadas.
- Esta selección añade ejercicios cortos de entrada, mapas, ficheros y
  excepciones para que no todo empiece directamente con GeoNotes.

UD00 no tiene calificación independiente. Estos ejercicios sirven para
diagnóstico, práctica y preparación de UD01/UD02a.

## Orden recomendado de los ZIP

Para publicar los proyectos Java en Moodle, usar este orden:

1. **`java25-labs-starters.zip`**: colección de retos cortos. Empezar por
   J25-01 y J25-03; continuar con J25-04 y J25-06; dejar J25-05 y J25-08 para
   cuando el grupo domine records, colecciones y tests.
2. **`calc25-starter.zip`**: proyecto de integración. El analizador ya existe
   y el alumnado añade funciones acotadas, historial, errores y tests de
   regresión.
3. **`geonotes-java25-starter.zip`**: proyecto más completo. Integra records,
   jerarquías `sealed`, `switch` con patrones, Streams, text blocks, ficheros
   y colecciones secuenciadas.

No es necesario publicar los tres ZIP a la vez. Lo recomendable es liberar el
primero, observar el diagnóstico y el ritmo de la clase, y publicar después
Calc25 y GeoNotes.
