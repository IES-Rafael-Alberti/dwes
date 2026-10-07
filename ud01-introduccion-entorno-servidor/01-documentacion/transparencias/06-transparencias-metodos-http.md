---
title: "UD01 — La intención de una petición"
subtitle: "06 · Métodos HTTP"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## El método no es decoración · 2 min

En `GET /api/hello`, **GET** expresa lo que el cliente solicita hacer y
`/api/hello` identifica el destino.

La semántica del método importa aunque el servidor acepte otras combinaciones.

*Base: [06 — Métodos HTTP](../06-metodos-http.md).*

## Dos propiedades distintas · 4 min

- **Seguro:** el cliente no pide cambiar el estado del recurso objetivo.
- **Idempotente:** repetir la misma operación tiene el mismo efecto previsto
  que enviarla una sola vez.

Una respuesta puede variar por hora o registro de actividad aunque el método
sea idempotente. **Seguro no significa «sin autenticación».**

*Base: [06 — «Seguro» e «Idempotente»](../06-metodos-http.md).*

## GET, HEAD y OPTIONS no piden cambios · 4 min

```http
GET /productos/42 HTTP/1.1
Host: tienda.example
Accept: application/json
```

- **GET** solicita una representación; `Accept` no garantiza recibir JSON.
- **HEAD** pide los campos de GET sin cuerpo de respuesta.
- **OPTIONS** consulta opciones; no presupongas que siempre aparece `Allow`.

Este catálogo es **hipotético**: Hello Server no ofrece `/productos/42`.

*Base: [06 — «Pedir información»](../06-metodos-http.md).*

## Una tabla para orientarse · 4 min

| Obtener/inspeccionar | Pedir cambios |
|---|---|
| GET y HEAD: seguros e idempotentes | PUT y DELETE: idempotentes, no seguros |
| OPTIONS: seguro e idempotente | POST: depende del procesamiento; PATCH: depende del cambio |

HEAD solicita los campos de GET **sin cuerpo de respuesta**. PUT expresa
reemplazo del recurso objetivo; PATCH aplica una modificación parcial.

*Base: [06 — Tabla de métodos](../06-metodos-http.md).*

## POST no equivale a «crear» · 3 min

POST pide al recurso que **procese** la representación enviada. Puede crear,
enviar una orden, iniciar una tarea o realizar otra operación definida por el
servidor.

```http
POST /pedidos HTTP/1.1
Host: tienda.example
Content-Type: application/json

{"productoId":42,"cantidad":1}
```

**Pregunta:** ¿por qué repetir un POST de pago puede ser diferente a repetir
un GET de una página?

*Base: [06 — «POST no equivale automáticamente a crear»](../06-metodos-http.md).*

## PUT, PATCH y DELETE al repetirlos · 4 min

- **PUT /productos/42** con el mismo estado: reemplazar dos veces deja el
  mismo resultado previsto, aunque el primer estado sea 201 y el segundo 200.
- **PATCH** fija o modifica parcialmente; su idempotencia depende del cambio:
  «fijar en 20» no es «sumar 1».
- **DELETE** repetido: el primer intento podría devolver 204 y el siguiente
  404; el recurso sigue ausente.

**No confundas «mismo efecto» con «misma respuesta HTTP».**

*Base: [06 — «Cambiar un recurso»](../06-metodos-http.md).*

## Observar sin asumir · 5 min

Con Hello Server disponible:

```bash
http --verbose GET http://localhost:8080/api/hello
http --verbose HEAD http://localhost:8080/api/hello
http --verbose OPTIONS http://localhost:8080/api/hello
```

Anota **lo que responde este servidor**: estado, cabeceras y cuerpo si lo
hay. `OPTIONS` no tiene por qué mostrar siempre la misma cabecera `Allow`.

*Base: [06 — «Exploración local»](../06-metodos-http.md).*

## Salida · 2 min

Clasifica GET, HEAD y POST según seguridad e idempotencia. Explica por qué
**idempotente no significa «todas las respuestas serán idénticas»**.

**Siguiente documento:** cómo comunica el servidor el resultado de procesar
una petición.
