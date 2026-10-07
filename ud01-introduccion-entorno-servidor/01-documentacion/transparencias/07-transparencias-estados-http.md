---
title: "UD01 — ¿Qué ocurrió con la petición?"
subtitle: "07 · Estados de respuesta HTTP"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## El estado es la primera pista · 2 min

El código de respuesta resume **el resultado de procesar la petición**.

- `2xx`: éxito; `3xx`: información para continuar o revalidar.
- `4xx`: la solicitud no se puede atender como se pidió.
- `5xx`: fallo atribuible al servidor.

El estado no sustituye al resto de cabeceras ni al cuerpo.

*Base: [07 — Estados HTTP](../07-estados-http.md).*

## Éxito no significa siempre «200 con cuerpo» · 4 min

| Estado | Qué indica |
|---|---|
| `200 OK` | Petición procesada correctamente |
| `201 Created` | Se creó algún recurso; puede haber `Location` |
| `204 No Content` | Éxito **sin contenido de respuesta** |
| `206 Partial Content` | Se entrega un rango de una representación |

**206 no es sinónimo de paginar una lista.**

*Base: [07 — Tabla de estados](../07-estados-http.md).*

## No mezclar «no modificado» y «no encontrado» · 3 min

- `304 Not Modified`: el cliente puede reutilizar una representación que ya
  tiene; está relacionado con una petición condicional y la caché.
- `404 Not Found`: no se proporciona la representación solicitada.

**Pregunta:** ¿por qué un `304` puede evitar descargar el mismo contenido?

*Base: [07 — Estados 304 y 404](../07-estados-http.md).*

## Tres errores que conviene distinguir · 4 min

- `400 Bad Request`: petición inválida o que no puede interpretarse.
- `401 Unauthorized`: faltan credenciales válidas de **autenticación**.
- `403 Forbidden`: petición comprendida, pero acceso **no autorizado**.

Otros ejemplos: `409` conflicto, `415` formato no admitido, `422` contenido
no procesable y `500` fallo interno. No exponer secretos ni trazas internas
en los errores.

*Base: [07 — Tabla y errores](../07-estados-http.md).*

## Comparar dos respuestas reales · 5 min

```bash
http --verbose GET http://localhost:8080/api/hello
http --verbose GET http://localhost:8080/no-existe
```

**Señala por separado:** estado, cabeceras y cuerpo. Describe lo observado en
Hello Server; no atribuyas a todos los servidores el mismo formato de error.

*Base: [07 — «Exploración local»](../07-estados-http.md).*

## Salida · 2 min

¿Qué aporta el estado y qué información necesitas consultar en las cabeceras
o el cuerpo para interpretar bien la respuesta?

**Siguiente documento:** los metadatos que acompañan al contenido.
