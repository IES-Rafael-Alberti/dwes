# Recordatorio: diseñar una base de datos relacional

## Proceso

1. Recoger requisitos y operaciones del sistema.
2. Identificar entidades, atributos y reglas del dominio.
3. Definir la clave primaria de cada entidad.
4. Representar las relaciones y sus cardinalidades.
5. Normalizar para evitar redundancia y anomalías de inserción, actualización y
   borrado.
6. Convertir el modelo conceptual en tablas, claves foráneas y restricciones.
7. Diseñar índices a partir de las consultas reales.
8. Probar inserciones válidas y rechazar estados imposibles.

## Ejemplo: biblioteca

Entidades principales:

- `book`: ISBN, título y disponibilidad.
- `user`: identidad y estado.
- `loan`: usuario, libro, fecha de préstamo y devolución.

Un usuario puede tener muchos préstamos y un libro puede aparecer en muchos
préstamos históricos. La relación se representa mediante la tabla `loan`, que
contiene las claves foráneas de `user` y `book`.

```sql
CREATE TABLE book (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL
);

CREATE TABLE app_user (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE loan (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    book_id BIGINT NOT NULL REFERENCES book(id),
    loaned_at TIMESTAMP NOT NULL,
    due_at TIMESTAMP NOT NULL,
    returned_at TIMESTAMP,
    CHECK (due_at >= loaned_at),
    CHECK (returned_at IS NULL OR returned_at >= loaned_at)
);

CREATE INDEX idx_loan_user_active ON loan(user_id, returned_at);
CREATE INDEX idx_loan_book_active ON loan(book_id, returned_at);
```

La base de datos relacional puede garantizar directamente claves, unicidad,
cardinalidad básica y algunas reglas mediante `NOT NULL`, `UNIQUE`, `CHECK` y
`FOREIGN KEY`. Las reglas que dependen de varias filas o de decisiones del
negocio también necesitan servicio y, en ocasiones, transacciones.

## Preguntas de revisión

- ¿Cuál es la clave primaria de cada tabla?
- ¿Qué relación representa cada clave foránea?
- ¿Qué ocurre si se borra un usuario con préstamos históricos?
- ¿Qué restricciones debe garantizar el SGBD?
- ¿Qué consultas justifican los índices?
