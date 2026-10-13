# Cuestionarios Moodle de UD01

La evaluación de UD01 combina una práctica cooperativa (**65 %**) y dos
cuestionarios individuales (**35 % en total**). Los cuestionarios tienen el
mismo peso dentro de su categoría (**50 % cada uno**).

## Bancos previstos para los cuestionarios

| Archivo GIFT | Actividad prevista | Cobertura | Preguntas | Categoría indicada en GIFT |
|---|---|---|---:|---|
| `ud01-2026-semana-01-http.gift` | Quiz 1 — HTTP | Mensajes, métodos, estados, cabeceras, cookies y seguridad básica | 16 | Categoría creada manualmente; importado y previsualizado en Moodle. |
| `ud01-2026-semana-02-entorno-tdd.gift` | Quiz 2 — Entorno y TDD | Java 25, Spring Boot inicial, Maven Wrapper, pruebas y ciclo red/green/refactor | 16 | `$course$/UD1/02 - Entorno y TDD` |

El banco de semana 01 no lleva directiva `$CATEGORY`: al importarlo ya se creó
su categoría manualmente. Los bancos de semana 02 y repaso sí incluyen la
categoría en el GIFT para que no se mezclen con otros bancos.

## Banco anterior de repaso

`ud01_repaso.gift` contiene 16 preguntas generales sobre cliente-servidor,
mecanismos, tecnologías, HTTP y seguridad. Se conserva como banco anterior de
repaso y se asigna a `$course$/UD1/03 - Repaso general`. No se cuenta como un
tercer cuestionario evaluable ni reemplaza a los dos cuestionarios previstos.

## Importar y validar en Moodle

Para importar semana 02 o el banco de repaso, usar el formato GIFT y activar la
opción **Obtener categorías del archivo** (el nombre puede variar ligeramente
según la versión/interfaz). La directiva `$CATEGORY` crea la ruta indicada si
no existe. Semana 01 ya tiene la categoría creada manualmente; evitar crearle
otra al volver a importarlo.

La categoría del GIFT se aplica **al importar**. Si semana 02 o el repaso ya se
importaron antes de añadir estas directivas, el fichero actualizado no mueve
automáticamente las preguntas existentes: habrá que moverlas desde el banco de
preguntas o reimportarlas en la categoría correcta, evitando duplicados.

El docente confirma que el banco HTTP de semana 01 ya fue importado y
previsualizado en Moodle; la categoría se creó manualmente. Para terminar de
validar el despliegue:

1. Importar semana 02 en el curso 2026/2027 con «Obtener categorías del
   archivo» activado y comprobar `UD1/02 - Entorno y TDD`.
2. Previsualizar sus 16 preguntas, incluidas selecciones múltiples,
   verdadero/falso, emparejamientos y huecos de código.
3. Hacer un intento de prueba de cada cuestionario como estudiante y revisar
   puntuación y retroalimentación.
4. Importar `ud01_repaso.gift` solo si se va a ofrecer como repaso; comprobar
   `UD1/03 - Repaso general` y mantenerlo sin un tercer peso evaluativo.
5. Registrar aquí las demás comprobaciones y correcciones.

**Estado:** semana 01 está importada y previsualizada. La validación completa
de semana 02, los intentos de prueba y la revisión final de puntuación siguen
pendientes. Los tres GIFT tienen 16 identificadores únicos. El GIFT se importa
como banco de preguntas; no se publica como adjunto descargable.

## Relación con la práctica final

La [práctica cooperativa HTTP + Hello Server](../03-ejercicios/03-practica-cooperativa-2026/README.md)
es el otro instrumento de UD01 y pesa 65 %. Los cuestionarios de esta carpeta
no la sustituyen ni crean una calificación adicional.
