# Diseño de bases de datos en MongoDB

Bloque de cinco sesiones para aprender a diseñar una base de datos documental
antes de escribir colecciones o endpoints.

## Secuencia de sesiones

| Sesión | Material | Idea principal |
|---:|---|---|
| 1 | [Modelado por acceso](01-modelado-acceso.md) | Diseñar a partir de consultas reales; decidir embedding o referencias. |
| 2 | [Validación e índices](02-validacion-indices.md) | Evitar el "schema de silencio" y garantizar consultas previsibles. |
| 3 | [Seguridad operativa](03-seguridad-operativa.md) | Usuarios, roles, copias y límites de exposición. |
| 4 | [Migraciones y versionado](04-migraciones-versionado.md) | Cambiar el esquema mediante scripts repetibles con `up` y `down`. |
| 5 | [Antipatrones](05-antipatrones.md) | Reconocer diseños que funcionan en una demo pero fallan al crecer. |

## La explicación de los primeros 10 minutos

MongoDB permite guardar documentos flexibles, pero esa flexibilidad no significa
que el diseño pueda improvisarse. Antes de crear colecciones hay que escribir:

1. Los casos de uso y las consultas principales.
2. Qué datos se leen siempre juntos y pueden embebirse.
3. Qué datos crecen mucho o se consultan aparte y deben referenciarse.
4. Los identificadores de cada documento y el sentido de cada referencia.
5. Las reglas que MongoDB no va a garantizar por sí sola.

Una referencia es solo un identificador almacenado en otro documento. MongoDB no
impide automáticamente que apunte a un documento inexistente, que se borre el
referenciado dejando huérfanos o que dos documentos contradigan la misma regla.
La aplicación debe validar esas operaciones, y el diseño debe acompañarse de
índices, validación `$jsonSchema`, transacciones cuando proceda y pruebas de
consistencia.

Esto no consiste en convertir MongoDB en SQL: se decide qué se embebe según el
acceso y el tamaño, y se referencian las relaciones que necesitan crecer o vivir
de forma independiente. La ausencia de claves foráneas hace más importante el
diseño previo, no menos.

## Ejercicios

- [Caso de blog](ejercicios/01-caso-blog.md)
- [Caso de e-commerce](ejercicios/02-caso-ecommerce.md)
- [Caso de refactor](ejercicios/03-caso-refactor.md)
- [Proyecto grupal de diseño](../../04-proyectos/01-diseno-mongo-grupal/README.md)
