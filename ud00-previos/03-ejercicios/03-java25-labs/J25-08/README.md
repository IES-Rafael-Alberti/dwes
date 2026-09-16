# J25-08 - Consulta geografica de GeoNotes

Implementa `GeoQueries.inside` con una lista de notas geolocalizadas.

- Devuelve los titulos de las notas dentro del rectangulo, incluidos sus
  bordes.
- Conserva el orden original.
- Ignora notas nulas.
- Usa `record patterns` para leer los datos de `Note` y `Point`.
- No modifiques la lista original.

Es una ampliacion pequena del dominio de GeoNotes: primero se resuelve con un
bucle legible y despues se compara con una version basada en Streams.
