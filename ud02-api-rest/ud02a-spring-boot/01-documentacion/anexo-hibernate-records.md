

> **Records → valores inmutables** (value objects).
> **Entities → objetos de identidad/vida propia** (normalmente clases).

Aquí va lo importante, con ejemplos.

---

# Soporte de `record` en Hibernate 6.2+

## 1) `@Embeddable` como valor (✅ encaje perfecto)

Los *records* funcionan **muy bien** como **tipos embebibles**: son inmutables, expresivos y su *canonical constructor* encaja con la instanciación que hace Hibernate.

```java
@Embeddable
public record Address(
  @Column(nullable = false) String street,
  @Column(nullable = false) String city,
  @Column(length = 10)       String zip
) {}
```

Uso dentro de una entidad:

```java
@Entity
public class Customer {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Embedded
  private Address address;   // inmutable, se reemplaza completo

  // getters/setters del campo "address" (reemplazo), no mutación interna
}
```

### Detalles prácticos

* **No necesitas** constructor sin argumentos. Hibernate usa el **canonical constructor** del record.
* **Inmutabilidad**: si cambias la dirección, **reemplazas el `Address` completo** (p. ej., `customer.setAddress(new Address(...))`).
* **Overrides**: si el embebible se usa varias veces:

  ```java
  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
    @AttributeOverride(name = "city",   column = @Column(name = "billing_city"))
  })
  private Address billingAddress;
  ```

🔎 **Limitaciones**:

* Los `@Embeddable` **no son perezosos** (se cargan con la entidad).
* Si todas (o algunas críticas) columnas del embebible son `NULL`, Hibernate suele **mapear el embebible a `null`** (no intenta construir un record “parcial”).

---

## 2) Clave compuesta con `@EmbeddedId` (✅ muy recomendable)

Otra combinación “10/10”: un *record* como **clave compuesta** inmutable. Su `equals/hashCode` generado es ideal para identificadores.

```java
@Embeddable
public record OrderLineId(Long orderId, Integer lineNo) {}

@Entity
public class OrderLine {
  @EmbeddedId
  private OrderLineId id;

  private String sku;
  private int quantity;
}
```

* Semánticamente claro: la **identidad** de `OrderLine` es `(orderId, lineNo)`.
* Sin boilerplate para `equals/hashCode`.

---

## 3) Entidades como `record` (⚠️ posible pero **no recomendado**)

Aunque Hibernate 6.x ha avanzado, mapear una **entidad entera** como `record` **sigue siendo frágil**:

* Las entidades suelen requerir **ciclo de vida mutable** (cambios campo a campo), *proxificación* para *lazy loading*, etc.
* Los *records* son **inmutables** y **no** tienen constructor vacío, lo cual **choca** con varios comportamientos tradicionales de JPA/Hibernate.
* Si fuerzas este enfoque, te verás empujado a **reemplazos completos de estado** y a escenarios más complejos con proxys.

👉 Recomendación pedagógica y práctica: **mantén las entidades como `class`** (con Lombok para evitar ruido) y reserva `record` para embebibles/DTOs/IDs.

---

## 4) Proyecciones y DTOs con `record` (✅ encaje ideal)

Para **lecturas** o **respuestas REST**, los *records* son perfectos:

* **Spring Data JPA (JPQL/HQL)**:

  ```java
  public record TaskView(Long id, String title, boolean done) {}

  @Query("select new com.example.TaskView(t.id, t.title, t.done) from Task t where t.done = :done")
  List<TaskView> findAllByDone(@Param("done") boolean done);
  ```

* **DTOs REST** (entrada/salida):

  ```java
  public record CreateTaskDTO(@NotBlank String title) {}
  public record TaskDTO(Long id, String title, boolean done) {}
  ```

* **Jackson** soporta records (2.12+); en la mayoría de casos no necesitas anotaciones extra.

---

## 5) `@Converter` y columnas “agregadas” (✅ avanzado opcional)

Si quieres **persistir un record como una sola columna** (p. ej., JSON o `VARCHAR` “compacto”), puedes usar un **`AttributeConverter`**:

```java
@Converter(autoApply = false)
public class MoneyJsonConverter implements AttributeConverter<Money, String> {
  @Override public String convertToDatabaseColumn(Money attr) {
    // serializa a JSON (o "100|EUR")
    return attr == null ? null : attr.amount() + "|" + attr.currency();
  }
  @Override public Money convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    var parts = dbData.split("\\|");
    return new Money(new BigDecimal(parts[0]), parts[1]);
  }
}

public record Money(BigDecimal amount, String currency) {}

@Entity
public class Invoice {
  @Id @GeneratedValue Long id;

  @Convert(converter = MoneyJsonConverter.class)
  @Column(length = 64)
  private Money total;
}
```

> Para datos **relacionales normales**, usa `@Embeddable` con varias columnas.
> Para un único campo “agregado” o **JSON**, el `@Converter` te da flexibilidad.

---

## 6) Buenas prácticas y checklist

* ✅ **Usa `record` para**:

  * `@Embeddable` (value objects).
  * `@EmbeddedId` (claves compuestas).
  * **DTOs** / **proyecciones** de solo lectura (Spring Data, REST).
* ⚠️ **Evita `record` en entidades**:

  * La inmutabilidad choca con el ciclo de vida JPA.
  * Proxies, *dirty checking* y *lazy loading* son más previsibles con `class`.
* ✅ Define **columnas no nulas** en embebibles *record* cuando el valor sea obligatorio.
* ✅ Para **reemplazar** un embebible, crea **una nueva instancia** del record y asigna el campo completo.
* ✅ Si el embebible se repite, usa **`@AttributeOverrides`**.
* ✅ Si quieres **un solo campo** para un record “agregado”, usa **`@Convert`**.

---

## 7) Mini ejemplos de bolsillo

### A) `@Embeddable` record

```java
@Embeddable
public record Geo(double lat, double lon) {}
```

```java
@Entity
public class Place {
  @Id @GeneratedValue Long id;
  @Embedded Geo position;
}
```

### B) `@EmbeddedId` record

```java
@Embeddable
public record EnrollmentId(Long studentId, Long courseId) {}

@Entity
public class Enrollment {
  @EmbeddedId EnrollmentId id;
  LocalDate enrolledOn;
}
```

### C) Proyección con record

```java
public record UserSummary(Long id, String username) {}

@Query("select new com.example.UserSummary(u.id, u.username) from User u")
List<UserSummary> summaries();
```

---

### Conclusión

* **Sí**, con Hibernate 6.2+ **puedes** usar *records* en JPA de forma muy cómoda para **`@Embeddable`** e **identificadores compuestos** (`@EmbeddedId`), y son **excelentes** como **DTOs/proyecciones**.
* Para **entidades completas**, mantente en `class` (Lombok) por estabilidad y ergonomía.

