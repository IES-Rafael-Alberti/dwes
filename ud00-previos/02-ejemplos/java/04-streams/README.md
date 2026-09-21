# Streams

Ejemplo breve de una transformación típica de datos: filtrar, mapear, ordenar,
agrupar y buscar un resultado opcional.

```bash
javac -d out src/*.java
java -cp out Main
```

En Spring Boot, este patrón aparece sobre todo al convertir resultados de
repositorio en DTOs. No sustituye a filtrar en la base de datos cuando el
volumen de datos es grande.
