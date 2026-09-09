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

## Estado de adopción en el curso

`QUERY` ya está definido por RFC 10008, pero clientes, CORS, proxies, cachés,
servidores y frameworks deben soportarlo durante todo el recorrido. En la
configuración actual del curso, Spring Boot `4.0.5` gestiona Spring Framework
`7.0.6` y Tomcat `11.0.20`; esa combinación no ofrece todavía
`RequestMethod.QUERY` ni soporte completo del contenedor para el cuerpo de una
petición `QUERY`.

Spring ha fusionado el soporte mínimo para Framework 7.1, previsto para la línea
7.1 y todavía no usado por nuestros proyectos. La API prevista usa
`@RequestMapping(method = RequestMethod.QUERY)` y `@RequestBody`; no se añade
`@QueryMapping`, porque ese nombre ya pertenece al ecosistema GraphQL.

Por tanto, el ejemplo de este bloque sigue siendo conceptual y no se incorpora
aún a Battleship ni a las prácticas evaluables. No se debe actualizar Spring
solo para probarlo ni implementar un filtro puente como solución general: si el
contenedor no reconoce `QUERY`, la petición puede rechazarse antes de llegar a
Spring MVC.

La planificación del curso es mantener `QUERY` como contenido teórico con
ejemplos de petición y respuesta. Si Spring Framework 7.1 se publica durante el
curso y la combinación concreta de Spring Boot, contenedor, cliente y pruebas
funciona de extremo a extremo, se podrá añadir una demostración aislada,
opcional y no evaluable. No sustituirá la práctica principal ni obligará a
actualizar sus dependencias.

La cabecera `Accept-Query` es una cabecera de respuesta de descubrimiento. Su
valor usa sintaxis de Structured Fields; por ejemplo, para anunciar JSON debe
escribirse `Accept-Query: "application/json"`, no asumir que funciona como una
cabecera libre de texto.

Referencias de seguimiento: [notas de Spring Framework
7.1](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.1-Release-Notes),
[PR de soporte](https://github.com/spring-projects/spring-framework/pull/34993) y
[RFC 10008](https://www.rfc-editor.org/info/rfc10008/).

## Idea clave

`QUERY` corrige el uso de `POST` para lecturas complejas, pero no reemplaza el
modelo de ejecución de GraphQL. En UD2 se estudia la diferencia; no se añade una
segunda tecnología obligatoria.
