---
title: "UD01 — Leer la conversación HTTP"
subtitle: "05 · Versiones y estructura de los mensajes"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## El contrato antes que los bytes · 3 min

Una interacción tiene **petición** y **respuesta**.

- El cliente indica qué solicita y aporta información.
- El servidor comunica qué ha ocurrido y devuelve una representación.
- Método, destino, estado, cabeceras y contenido conservan su sentido aunque
  cambie la versión de HTTP.

*Base: [05 — Versiones y mensajes HTTP](../05-versiones-y-mensajes-http.md).*

## Versiones: ¿es HTTP «texto»? · 3 min

| Versión | Lo importante hoy |
|---|---|
| HTTP/1.1 | Ejemplos legibles como texto; habitualmente TCP |
| HTTP/2 | Frames binarios y varias respuestas sobre TCP |
| HTTP/3 | Frames binarios y flujos QUIC sobre UDP |

**No:** describir todo HTTP como un protocolo de texto. **Sí:** usar
HTTP/1.1 para aprender la estructura lógica.

*Base: [05 — «Versiones HTTP»](../05-versiones-y-mensajes-http.md).*

## TCP, UDP y QUIC en una frase · 3 min

- **TCP:** entrega bytes de forma fiable y en orden.
- **UDP:** envía datagramas sin asegurar por sí solo entrega u orden.
- **QUIC:** usa UDP y añade entrega controlada, conexión segura y varios flujos.

**HTTP/3 no es menos fiable por usar UDP:** QUIC aporta las funciones
necesarias. Aquí no implementamos ninguno de estos transportes.

*Base: [05 — «Vocabulario mínimo»](../05-versiones-y-mensajes-http.md).*

## Frames, multiplexación y cabeceras · 3 min

- **Frame binario:** pieza del mensaje estructurada para que la procese el
  programa, no una línea de texto como en los ejemplos HTTP/1.1.
- **Multiplexar:** alternar HTML, CSS e imágenes en la misma conexión.
- **Comprimir cabeceras:** codificar campos repetidos en menos espacio; es
  distinto de multiplexar o comprimir el cuerpo.

En QUIC, perder datos de un flujo no obliga a frenar la entrega de **otro**;
los flujos todavía comparten red y recursos.

*Base: [05 — «Vocabulario mínimo»](../05-versiones-y-mensajes-http.md).*

## Anatomía de la petición · 4 min

```http
GET /api/hello HTTP/1.1
Host: localhost:8080
Accept: application/json

```

Separa **método**, **destino**, **cabeceras** y **contenido opcional**. La
línea en blanco cierra las cabeceras; en este ejemplo no se envía cuerpo.

*Base: [05 — «Estructura de una petición»](../05-versiones-y-mensajes-http.md).*

## Anatomía de la respuesta · 4 min

```http
HTTP/1.1 200 OK
Content-Type: application/json

{"message":"Hola desde el servidor"}
```

**200** comunica el resultado; `Content-Type` describe cómo interpretar el
**cuerpo**, que aquí es JSON.

*Base: [05 — «Estructura de una respuesta»](../05-versiones-y-mensajes-http.md).*

## Una traza real con HTTPie · 7 min

Con [Hello Server](../../02-ejemplos/hello-server/README.md) iniciado:

```bash
http --verbose GET http://localhost:8080/api/hello
```

Señala en pantalla método, destino, cabeceras enviadas, estado, cabeceras de
respuesta y cuerpo. Después localiza lo mismo en **Red** del navegador.

**Si no arranca el ejemplo:** usar el intercambio escrito en el documento 05;
retomar la traza real cuando esté preparado el entorno.

*Base: [05 — «Comprobación»](../05-versiones-y-mensajes-http.md).*

## Salida · 2 min

¿Qué puedes afirmar viendo solo `200`? ¿Qué necesitas mirar para saber si la
respuesta es HTML o JSON?

**Siguiente documento:** el método expresa la intención de la petición; la
URL y el cuerpo no la sustituyen.
