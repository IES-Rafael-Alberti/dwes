# Versiones y estructura de los mensajes HTTP

HTTP define la conversación entre clientes y servidores. La versión cambia la
forma de transportar los mensajes, pero mantiene la semántica de métodos,
estados, cabeceras y contenido.

## Versiones HTTP

| Versión | Transporte y representación | Aporte principal |
|---|---|---|
| HTTP/1.1 | Habitualmente TCP; mensajes reconocibles como texto | Conexiones persistentes y semántica web moderna |
| HTTP/2 | TCP; framing binario | Multiplexación y compresión de campos |
| HTTP/3 | QUIC sobre UDP; framing binario | Flujos independientes y TLS integrado en QUIC |

HTTP no es genéricamente un protocolo de texto: esa descripción solo ayuda a
leer HTTP/1.x. HTTP/2 y HTTP/3 transportan la misma semántica mediante frames
binarios.

## Estructura de una petición

Una petición contiene método, destino, cabeceras y contenido opcional:

```http
GET /api/hello HTTP/1.1
Host: localhost:8080
Accept: application/json

```

La línea inicial identifica método, destino y versión. Las cabeceras aportan
metadatos. La línea en blanco separa las cabeceras del contenido, que en este
caso no existe.

## Estructura de una respuesta

```http
HTTP/1.1 200 OK
Content-Type: application/json

{"message":"Hola desde el servidor"}
```

La respuesta contiene estado, cabeceras y contenido opcional. `Content-Type`
indica cómo interpretar el contenido recibido. El navegador oculta buena parte
de este intercambio; HTTPie y la pestaña **Red** permiten observarlo.

## Comprobación

Captura con HTTPie una petición a `/api/hello` y señala método, destino,
cabeceras, estado y contenido. Después localiza los mismos elementos en las
herramientas de desarrollo del navegador.
