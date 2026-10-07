# Versiones y estructura de los mensajes HTTP

HTTP define la conversación entre clientes y servidores. La versión cambia la
forma de transportar los mensajes, pero mantiene la semántica de métodos,
estados, cabeceras y contenido.

## Versiones HTTP

| Versión | Transporte y representación | Aporte principal |
|---|---|---|
| HTTP/1.1 | Habitualmente TCP; mensajes reconocibles como texto | Conexiones persistentes y semántica web moderna |
| HTTP/2 | TCP; mensajes divididos en frames binarios | Multiplexación de peticiones/respuestas y compresión de cabeceras |
| HTTP/3 | QUIC sobre UDP; frames binarios | Flujos que se transportan de forma independiente y cifrado integrado |

HTTP no es genéricamente un protocolo de texto: esa descripción solo ayuda a
leer HTTP/1.x. HTTP/2 y HTTP/3 transportan la misma semántica mediante frames
binarios.

### Vocabulario mínimo para leer la tabla

- **TCP** transporta una secuencia de bytes de forma fiable y en orden entre
  dos extremos. Si faltan bytes, los recupera antes de entregar los siguientes
  en esa secuencia. HTTP/1.1 y HTTP/2 suelen usarlo.
- **UDP** envía datagramas: por sí solo no garantiza que lleguen ni en qué orden.
  Eso no hace que HTTP/3 sea «poco fiable»: **QUIC**, construido sobre UDP,
  aporta control de entrega, conexión segura y otras funciones que HTTP necesita.
- Un **frame binario** es una pieza estructurada del mensaje, con campos que
  procesa el programa; no es una línea de texto legible como las del ejemplo
  HTTP/1.1. HTTPie o el navegador pueden mostrar los datos **interpretados**,
  aunque en la red se hayan transmitido frames binarios.
- **Multiplexar** es alternar varias peticiones y respuestas lógicas en una
  misma conexión. Por ejemplo, una página puede solicitar HTML, CSS e imágenes
  sin esperar a que termine completamente cada respuesta para iniciar la
  siguiente. No significa «multiplexar cabeceras».
- Un **flujo** es una de esas conversaciones lógicas dentro de la conexión. En
  HTTP/3, QUIC permite que la pérdida de datos de un flujo no obligue a esperar
  esos datos para entregar los de otro. Comparten red y recursos: «independiente»
  no significa ausencia de límites o retrasos.
- La **compresión de campos** codifica cabeceras repetidas (`Host`,
  `Content-Type`...) de forma más compacta. Es distinta de multiplexar y no
  significa comprimir automáticamente el cuerpo HTML o JSON.

Para esta unidad basta con distinguir **qué conserva HTTP** (método, estado,
cabeceras y contenido) y **qué cambia su transporte**. No hace falta programar
TCP, UDP, QUIC ni el formato de sus frames para interpretar una traza HTTP.

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
