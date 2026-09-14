# De tablas SQL a entidades JPA

Esta es una primera orientación. La configuración, el ciclo de vida, las
consultas y los problemas de carga se estudiarán con detalle en Spring Boot.

## Correspondencias básicas

| SQL | JPA |
|---|---|
| tabla | `@Entity` y `@Table` |
| clave primaria | `@Id` |
| identidad generada | `@GeneratedValue` |
| columna | atributo y `@Column` |
| `NOT NULL` | `nullable = false` y validación de entrada |
| `UNIQUE` | `unique = true` o restricción/migración SQL |
| FK `N:1` | `@ManyToOne` y `@JoinColumn` |
| relación `1:N` | `@OneToMany(mappedBy = "...")` |

## Ejemplo mínimo

```java
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String title;
}
```

La relación `loan.book_id -> book.id` puede representarse así:

```java
@Entity
@Table(name = "loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
```

Puntos importantes para no automatizar sin pensar:

- La entidad no sustituye al modelo relacional: las FK y restricciones siguen
  siendo importantes.
- `LAZY` evita cargar relaciones grandes sin pedirlas, pero obliga a diseñar
  correctamente el caso de uso y la consulta.
- Las migraciones Flyway/Liquibase deben ser la fuente explícita del esquema en
  producción; no conviene depender solo de que Hibernate cree tablas.
- DTOs y validación de entrada protegen la API, pero no sustituyen las
  restricciones de la base de datos.
- `cascade` y `orphanRemoval` cambian el ciclo de vida: no se añaden por defecto.

La documentación oficial de Spring Data JPA sirve como referencia técnica,
pero en clase se trabajará el mapeo a partir de un diseño SQL ya razonado.
