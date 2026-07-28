# GraphQL y el método HTTP QUERY

GraphQL permite que el cliente describa los datos que necesita mediante un
esquema tipado. Para UD2 basta con reconocer esa idea y compararla con REST y
con el método `QUERY` definido por [RFC 10008](https://www.rfc-editor.org/rfc/rfc10008.html)
en junio de 2026.

## El problema

Una consulta compleja no siempre cabe de forma legible en una URL:

```http
GET /games?status=ACTIVE&player=42&sort=-createdAt&page=0&size=20
```

Enviar los filtros mediante `POST` permite usar un cuerpo, pero el método no
declara que la operación sea segura e idempotente. Un `GET` con cuerpo tampoco
es una solución: HTTP no define semántica general para ese contenido.

## QUERY: una consulta segura con cuerpo

`QUERY` permite enviar una representación de la consulta en el cuerpo y declara
la operación como segura, idempotente y cacheable:

```http
QUERY /games HTTP/1.1
Content-Type: application/json

{
  "status": "ACTIVE",
  "playerId": 42,
  "sort": ["createdAt,desc"],
  "page": 0,
  "size": 20
}
```

El servidor debe interpretar el cuerpo según su `Content-Type`. La clave de
caché debe incorporar ese contenido; no basta con la URI.

## Ejemplo mínimo de GraphQL

Una consulta GraphQL expresa tanto el filtro como los campos de respuesta:

```graphql
query ActiveGames($playerId: ID!) {
  games(status: ACTIVE, playerId: $playerId) {
    id
    status
    createdAt
  }
}
```

```json
{
  "playerId": "42"
}
```

Su valor no está solo en transportar filtros. GraphQL añade un esquema tipado,
selección de campos, validación de consultas y resolvers capaces de componer
datos. `QUERY` no proporciona esas capacidades: define semántica HTTP para una
consulta segura con cuerpo.

## Qué elegir

| Necesidad | Opción adecuada |
| --- | --- |
| Recuperar un recurso o listado sencillo | `GET` REST |
| Enviar filtros complejos en el cuerpo sin modificar estado | `QUERY`, cuando toda la infraestructura lo soporte |
| Permitir selección y composición flexible sobre un esquema | GraphQL |
| Crear o ejecutar una operación con efectos | `POST` |

## Límite actual

`QUERY` es un estándar nuevo. Clientes, CORS, proxies, cachés, servidores y
frameworks deben soportarlo durante todo el recorrido. Spring Framework 7.0,
usado por Spring Boot 4.0, todavía no ofrece un valor declarativo
`RequestMethod.QUERY`; el proyecto Spring está trabajando en ese soporte para
una versión posterior. Por eso el ejemplo es conceptual y no se incorpora aún
a Battleship.

## Idea clave

`QUERY` corrige el uso de `POST` para lecturas complejas, pero no reemplaza el
modelo de ejecución de GraphQL. En UD2 se estudia la diferencia; no se añade una
segunda tecnología obligatoria.
