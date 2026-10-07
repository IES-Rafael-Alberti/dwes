---
title: "UD01 — Lo que cuentan las cabeceras"
subtitle: "08 · Contenido, caché y negociación"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## Cabeceras ≠ cuerpo · 2 min

Las cabeceras son **metadatos** de una petición o respuesta. El cuerpo contiene
la representación, si la hay.

- `Host` identifica el destino en HTTP/1.1.
- `Content-Length` describe el tamaño cuando se conoce.
- `Location` puede indicar otra URI.

*Base: [08 — «Cabeceras esenciales»](../08-cabeceras-http-contenido-y-cache.md).*

## Lo que acepto frente a lo que envío · 4 min

- `Accept` expresa **formatos que el cliente puede procesar**.
- `Content-Type` describe **el formato del contenido enviado** en la
  petición o en la respuesta.

```http
Accept: application/json
Content-Type: application/json
```

Que una ruta se llame `/api` no determina el formato de su respuesta.

*Base: [08 — «Cabeceras esenciales»](../08-cabeceras-http-contenido-y-cache.md).*

## Caché: ¿puedo reutilizar lo que ya tengo? · 4 min

- `Cache-Control` expresa directivas para almacenar o reutilizar.
- `ETag` identifica una representación concreta.
- `If-None-Match` permite pedirla **condicionalmente**.
- Un `304 Not Modified` puede evitar volver a transferir el contenido.

**No presupongas que Hello Server implementa `ETag`: aquí estudiamos el
mecanismo, no una respuesta garantizada por este ejemplo.**

*Base: [08 — «Caché y validación condicional»](../08-cabeceras-http-contenido-y-cache.md).*

## Comparar HTML y JSON · 5 min

```bash
http --headers GET http://localhost:8080/
http --body GET http://localhost:8080/
http --headers GET http://localhost:8080/api/hello
http --body GET http://localhost:8080/api/hello
```

Relaciona cada `Content-Type` con el cuerpo observado. Contrasta en el
navegador con **F12 → Red → Encabezados / Respuesta**.

*Base: [08 — «Exploración con HTTPie y navegador»](../08-cabeceras-http-contenido-y-cache.md).*

## Al compartir una traza · 3 min

`Set-Cookie`, `Cookie` y `Authorization` pueden revelar sesiones o
credenciales. Si compartes una salida, conserva el **nombre** de la cabecera
y reemplaza su valor por `<REDACTED>`.

*Base: [08 — «Cabeceras esenciales»](../08-cabeceras-http-contenido-y-cache.md)
y [seguridad HTTP](../../06-seguridad/README.md).*

## Salida · 2 min

Si un cliente acepta JSON, ¿qué cabecera miras para confirmar que la respuesta
recibida es JSON? ¿En qué se diferencia del cuerpo?

**El siguiente tema, QUERY, se tratará aparte y no requiere adelantarlo hoy.**
