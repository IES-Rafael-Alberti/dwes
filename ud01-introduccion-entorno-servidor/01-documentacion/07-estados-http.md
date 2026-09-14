# Estados de respuesta HTTP: 2xx, 3xx, 4xx y 5xx

El estado comunica el resultado de procesar una petición. No sustituye al
cuerpo de respuesta, pero permite al cliente decidir cómo continuar.

| Estado | Significado |
|---|---|
| 200 OK | La petición se procesó correctamente |
| 201 Created | Se creó uno o más recursos; puede identificarse con `Location` |
| 204 No Content | Éxito sin contenido de respuesta |
| 206 Partial Content | Se entrega uno o más rangos de una representación |
| 304 Not Modified | Se puede reutilizar una representación almacenada |
| 400 Bad Request | La petición es inválida o no puede interpretarse |
| 401 Unauthorized | Faltan credenciales válidas de autenticación |
| 403 Forbidden | La petición se entiende, pero no está autorizada |
| 404 Not Found | No se proporciona una representación del recurso objetivo |
| 409 Conflict | La petición entra en conflicto con el estado actual |
| 415 Unsupported Media Type | El formato del contenido no está soportado |
| 422 Unprocessable Content | El formato se reconoce, pero no puede procesarse |
| 500 Internal Server Error | Fallo inesperado atribuible al servidor |

`206 Partial Content` describe rangos de una representación y no equivale a la
paginación de una colección. El cuerpo de error debe ser útil sin filtrar
trazas, secretos ni estructura interna.

## Exploración local

```bash
http --verbose GET http://localhost:8080/api/hello
http --verbose GET http://localhost:8080/no-existe
```

Compara `200` y `404`, y explica qué parte de la evidencia procede del estado,
qué parte de las cabeceras y qué parte del cuerpo.
