# J25-06 - Fichero de notas

Implementa `NoteFile.save` y `NoteFile.load` usando `Path` y `Files`.

- Guarda una nota por linea con el formato `titulo<TAB>contenido`.
- Crea las carpetas padre si no existen.
- Descarta lineas vacias al leer.
- Rechaza notas sin titulo y propaga un error claro si el fichero no es valido.
- Usa UTF-8 de forma explicita.

El reto actualiza el capitulo de ficheros del libro a NIO.2 y practica una
separacion pequena entre formato y acceso a disco.
