# HTTP actual: semántica, versiones y QUERY

HTTP define la semántica de peticiones y respuestas. La versión de transporte cambia cómo se codifican y multiplexan los mensajes, pero un `GET`, un estado `404` o una cabecera `Content-Type` mantienen su significado entre HTTP/1.1, HTTP/2 y HTTP/3.

## Versiones

| Versión | Transporte y representación | Aporte principal |
|---|---|---|
| HTTP/1.1 | Habitualmente TCP; mensajes reconocibles como texto | Conexiones persistentes y semántica web moderna |
| HTTP/2 | TCP; framing binario | Multiplexación y compresión de campos |
| HTTP/3 | QUIC sobre UDP; framing binario | Flujos independientes y TLS integrado en QUIC |

HTTP no es genéricamente "un protocolo de texto": esa descripción solo ayuda a leer HTTP/1.x. HTTP/2 y HTTP/3 transportan la misma semántica mediante frames binarios.

## Estructura lógica

Una petición contiene:

- método;
- destino de la petición;
- campos o cabeceras;
- contenido opcional cuando el método y el recurso le asignan semántica.

Una respuesta contiene:

- estado;
- campos o cabeceras;
- contenido opcional.

`Content-Type` describe el formato del contenido tanto en peticiones como en respuestas. `Accept` permite al cliente expresar qué formatos puede procesar.

## Propiedades de los métodos

| Método | Seguro | Idempotente | Uso típico |
|---|---|---|---|
| GET | Sí | Sí | Obtener una representación |
| HEAD | Sí | Sí | Obtener los mismos campos que GET sin contenido de respuesta |
| POST | No necesariamente | No necesariamente | Solicitar un procesamiento definido por el recurso |
| PUT | No | Sí | Crear o reemplazar el estado del recurso objetivo |
| PATCH | No | No necesariamente | Aplicar una modificación parcial |
| DELETE | No | Sí | Solicitar la eliminación del recurso objetivo |
| OPTIONS | Sí | Sí | Consultar opciones de comunicación |
| QUERY | Sí | Sí | Ejecutar una consulta descrita en el contenido |

**Seguro** significa que el cliente no solicita un cambio de estado en el recurso objetivo. **Idempotente** significa que repetir una petición con la misma intención produce el mismo efecto previsto que enviarla una vez. No significa que cada respuesta sea idéntica ni que el servidor no registre actividad.

POST no equivale automáticamente a "crear". Su semántica depende del recurso. Del mismo modo, diseñar CRUD no basta para diseñar una interfaz HTTP coherente.

## QUERY

RFC 10008, publicado en junio de 2026, define QUERY para consultas cuya descripción viaja en el contenido de la petición:

```http
QUERY /productos HTTP/1.1
Host: example.org
Content-Type: application/json

{"precioMaximo": 50, "etiquetas": ["local", "eco"]}
```

QUERY cubre el espacio entre GET y el uso ambiguo de POST para búsquedas complejas:

- es seguro e idempotente;
- espera contenido con un `Content-Type` coherente;
- permite reintentos automáticos desde el punto de vista semántico;
- sus respuestas son cacheables, aunque la clave debe incorporar contenido y metadatos;
- puede evitar colocar criterios sensibles o voluminosos en la URI, pero el contenido también debe protegerse y puede ser registrado.

Que el método esté estandarizado no implica que todos los frameworks, proxies y clientes lo soporten ya. En UD1 se estudia su semántica; cualquier implementación posterior deberá verificar toda la cadena.

## Estados que suelen confundirse

| Estado | Significado |
|---|---|
| 200 OK | La petición se procesó correctamente |
| 201 Created | Se creó uno o más recursos; la respuesta puede identificar el recurso principal con `Location` |
| 204 No Content | Éxito sin contenido de respuesta |
| 206 Partial Content | Se entrega uno o más rangos de una representación; no representa paginación de una colección |
| 304 Not Modified | Una petición condicional puede reutilizar su representación almacenada |
| 400 Bad Request | La petición es inválida o no puede interpretarse correctamente |
| 401 Unauthorized | Faltan credenciales válidas de autenticación |
| 403 Forbidden | La petición se entiende, pero no está autorizada |
| 404 Not Found | No se proporciona una representación del recurso objetivo |
| 409 Conflict | La petición entra en conflicto con el estado actual |
| 415 Unsupported Media Type | El formato del contenido no está soportado |
| 422 Unprocessable Content | El contenido tiene formato reconocido, pero sus instrucciones no pueden procesarse |
| 500 Internal Server Error | Fallo inesperado atribuible al servidor |

Los estados describen el resultado HTTP. El cuerpo de error debe aportar detalles útiles sin filtrar trazas, secretos o estructura interna.

## Caché y validación condicional

La caché no consiste solo en guardar respuestas durante un tiempo. Campos como `Cache-Control`, `ETag`, `If-None-Match`, `Last-Modified` e `If-Modified-Since` permiten reutilizar o revalidar representaciones.

Una respuesta `304 Not Modified` evita transferir de nuevo el contenido. Una `206 Partial Content` responde a una petición de rango y debe incluir la información de rango correspondiente.

## Referencias normativas

- [RFC 9110 - HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110.html)
- [RFC 9113 - HTTP/2](https://www.rfc-editor.org/rfc/rfc9113.html)
- [RFC 9114 - HTTP/3](https://www.rfc-editor.org/rfc/rfc9114.html)
- [RFC 10008 - The HTTP QUERY Method](https://www.rfc-editor.org/rfc/rfc10008.html)

## Comprobación

Clasifica una petición de cada método según seguridad e idempotencia y justifica por qué 206 no sustituye la paginación y QUERY no es simplemente POST con otro nombre.
