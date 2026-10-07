---
title: "UD01 — Consultar con contenido"
subtitle: "09 · Método HTTP QUERY (idea conceptual)"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## Un problema de tamaño y semántica · 3 min

- `GET /productos?...` expresa una consulta en la URI: puede hacerse larga.
- Con `POST` se puede enviar una consulta en el cuerpo, pero el método **no
  expresa por sí mismo** que no va a modificar el recurso.
- **QUERY** pide procesar una consulta enviada en el contenido de la petición.

*Base: [09 — QUERY HTTP](../09-query-http.md) y [RFC 10008](https://www.rfc-editor.org/rfc/rfc10008.html).*

## La petición tiene contenido · 4 min

```http
QUERY /productos HTTP/1.1
Host: example.org
Content-Type: application/json

{"precioMaximo":50,"etiquetas":["local","eco"]}
```

El cuerpo describe la consulta; `Content-Type` declara su formato. Sin un
tipo coherente con el contenido, el servidor debe rechazar la petición.

*Base: [09 — Ejemplo](../09-query-http.md).*

## ¿Qué garantiza el método? · 3 min

Según la RFC 10008, QUERY es **seguro e idempotente**: el cliente no pide
modificar el estado del recurso objetivo y puede repetir la operación.

**No significa** que ejecutar una consulta sea gratis ni que dos respuestas
obtenidas en momentos distintos deban ser idénticas.

*Base: [09 — Semántica](../09-query-http.md).*

## Dos cabeceras con cometidos distintos · 3 min

- `Content-Type`: formato de la consulta que **envía el cliente**.
- `Accept-Query`: formatos de consulta que **puede anunciar el servidor**;
  es un campo estructurado. No es el `Accept` con el que el **cliente** indica
  qué formatos de respuesta acepta.

```http
Accept-Query: "application/json"
```

*Base: [09 — Accept-Query](../09-query-http.md).*

## ¿Por qué no probarlo hoy? · 3 min

- Que QUERY esté estandarizado **no** asegura que lo admitan nuestro cliente,
  proxies, Tomcat y la aplicación.
- Hello Server **no implementa** un endpoint QUERY; no fingimos una demo.
- Caché y registros también deben considerar el contenido de la consulta.

En UD01 basta comprender **para qué existe** y distinguirlo de GET y POST.
No es un método evaluable de forma práctica en este recorrido.

*Base: [09 — Límites de adopción](../09-query-http.md).*

## Salida · 2 min

Una consulta no cabe bien en la URI y no modifica el recurso. ¿Qué expresa
QUERY que un POST genérico no expresa por sí solo?

**Siguiente paso:** preparar el entorno del ejemplo Java que sí ejecutamos.
