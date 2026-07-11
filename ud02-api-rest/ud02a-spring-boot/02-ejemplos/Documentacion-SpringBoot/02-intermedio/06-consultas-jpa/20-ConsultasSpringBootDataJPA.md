---
title: "Consultas en Spring Boot con Spring Data JPA"
subtitle: "Derived Queries, Query con JPQL, Specifications y otras alternativas"
author: "José Manuel Sánchez Álvarez"
center: "IES Rafael Alberti"
course: "Desarrollo Web en Entorno Servidor — Curso 2025–2026"
lang: "es"
---

# 🧩 Consultas en Spring Boot con Spring Data JPA

Spring Data JPA simplifica el acceso a bases de datos en aplicaciones Java mediante un enfoque declarativo.  
En lugar de escribir SQL manualmente, podemos definir métodos en interfaces de repositorio que **Spring interpreta automáticamente**.

---

## 1. Consultas derivadas (Derived Queries)

Spring Data JPA permite crear consultas **a partir del nombre de los métodos** del repositorio.  
El motor analiza el nombre del método y genera la consulta JPQL correspondiente.

### 🧠 Concepto
Los métodos comienzan con palabras clave como:

`findBy`, `readBy`, `queryBy`, `countBy`, `existsBy`, seguidas del nombre del campo o combinación de campos.

#### 🧩 Ejemplo básico

```java
// UserRepository.java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByLastName(String lastName);
}
````

**Equivalente JPQL:**

```sql
SELECT u FROM User u WHERE u.lastName = ?1
```

---

### ⚙️ Operadores admitidos

| Operador                  | Ejemplo                                | Descripción             |
| ------------------------- | -------------------------------------- | ----------------------- |
| `And`, `Or`               | `findByNameAndAge`                     | Combina condiciones     |
| `Between`                 | `findByAgeBetween(int min, int max)`   | Rango de valores        |
| `LessThan`, `GreaterThan` | `findBySalaryGreaterThan(double min)`  | Comparaciones           |
| `Like`, `NotLike`         | `findByNameLike("%Juan%")`             | Coincidencias parciales |
| `In`, `NotIn`             | `findByStatusIn(List<String> estados)` | Listas de valores       |
| `IsNull`, `IsNotNull`     | `findByEmailIsNotNull()`               | Campos nulos/no nulos   |
| `OrderBy`                 | `findByActiveTrueOrderByLastNameAsc()` | Ordenación directa      |

---

### 🧩 Ejemplo con varios criterios

```java
List<User> findByFirstNameAndAgeGreaterThan(String name, int age);
```

→ `SELECT u FROM User u WHERE u.firstName = ?1 AND u.age > ?2`

---

### 🔗 Consultas con relaciones (JOIN implícito)

Cuando la entidad tiene relaciones (OneToMany, ManyToOne, etc.), Spring puede navegar por ellas:

```java
List<Order> findByCustomerName(String name);
```

Si `Order` tiene un campo `Customer customer`, Spring entiende:

```sql
SELECT o FROM Order o JOIN o.customer c WHERE c.name = ?1
```

#### 🔍 Ejemplo con relaciones anidadas

```java
List<Order> findByCustomerAddressCity(String city);
```

→ `SELECT o FROM Order o JOIN o.customer c JOIN c.address a WHERE a.city = ?1`

---

## 2. Consultas personalizadas con `@Query` (JPQL)

Cuando las consultas derivadas no son suficientes o se vuelven confusas, podemos usar `@Query`.

### 🧠 ¿Qué es JPQL?

JPQL (*Java Persistence Query Language*) es similar a SQL, pero opera sobre **entidades y atributos**, no sobre tablas ni columnas físicas.

| Aspecto       | SQL                                | JPQL                  |
| ------------- | ---------------------------------- | --------------------- |
| Opera sobre   | Tablas y columnas                  | Entidades y atributos |
| Portabilidad  | Dependiente del gestor             | Independiente         |
| Legibilidad   | Técnica                            | Orientada a objetos   |
| Mantenimiento | Requiere cambios si cambia el SGBD | Generalmente estable  |

---

#### 🧩 Ejemplo simple con `@Query`

```java
@Query("SELECT u FROM User u WHERE u.lastName = :lastName")
List<User> findByLastName(@Param("lastName") String lastName);
```

**Equivalente SQL (para PostgreSQL):**

```sql
SELECT * FROM users WHERE last_name = 'Pérez';
```

---

### ⚙️ Consultas con múltiples parámetros y ordenación

```java
@Query("SELECT u FROM User u WHERE u.age > :age ORDER BY u.lastName ASC")
List<User> findOlderThan(@Param("age") int age);
```

---

### ⚡ Consultas con JOIN

```java
@Query("""
       SELECT o FROM Order o
       JOIN o.customer c
       WHERE c.city = :city AND o.total > :minTotal
       """)
List<Order> findOrdersByCustomerCityAndMinTotal(
        @Param("city") String city,
        @Param("minTotal") double minTotal);
```

---

## 3. Consultas nativas con SQL

Spring también permite usar **SQL puro** si es necesario.

```java
@Query(value = "SELECT * FROM users WHERE last_name = :lastName", nativeQuery = true)
List<User> findByLastNameNative(@Param("lastName") String lastName);
```

**⚠️ Inconveniente:**
Esta consulta depende del dialecto del motor (PostgreSQL, MySQL, Oracle, etc.).
Si el proyecto cambia de SGBD, puede requerir modificaciones.

---

## 4. Spring Data JPA Specifications

Las **Specifications** permiten construir consultas dinámicas y combinarlas mediante una API basada en predicados.
Útiles en escenarios de filtros opcionales o búsqueda avanzada.

#### 🧩 Ejemplo

```java
public class UserSpecifications {
    public static Specification<User> hasLastName(String lastName) {
        return (root, query, cb) -> cb.equal(root.get("lastName"), lastName);
    }

    public static Specification<User> isAdult() {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("age"), 18);
    }
}
```

Uso:

```java
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {}
```

```java
List<User> results = userRepository.findAll(
    Specification.where(UserSpecifications.hasLastName("Pérez"))
                 .and(UserSpecifications.isAdult())
);
```

---

## 5. Otras alternativas

### 🧰 `JdbcTemplate`

Permite ejecutar sentencias SQL directamente.
Útil para operaciones simples o cuando se necesita máximo control.

```java
@Autowired
private JdbcTemplate jdbcTemplate;

public List<User> findAll() {
    return jdbcTemplate.query("SELECT * FROM users",
        (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("name")));
}
```

---

### ⚙️ Dynamic Predicates

Permiten construir filtros condicionales sobre la marcha,
generalmente combinando `CriteriaBuilder` y `Predicate`.

---

### 🧱 QueryDSL

Ofrece una API **tipada y fluida** para construir consultas,
con validación en tiempo de compilación.

```java
QUser user = QUser.user;
List<User> adults = queryFactory.selectFrom(user)
    .where(user.age.gt(18))
    .fetch();
```

---

### 🪶 Micronaut Data

Alternativa moderna a Spring Data, con compilación **AOT (Ahead-Of-Time)**
y excelente rendimiento, aunque con menor madurez de ecosistema.

---

## 🧭 Cuándo usar cada enfoque

| Enfoque                       | Cuándo usarlo                           | Ventajas                | Inconvenientes                    |
| ----------------------------- | --------------------------------------- | ----------------------- | --------------------------------- |
| **Derived Queries**           | Consultas simples                       | Rápido, limpio          | Limitado para consultas complejas |
| **@Query (JPQL)**             | Consultas personalizadas medianas       | Portabilidad, claridad  | Mantenimiento manual              |
| **@Query (SQL nativo)**       | Optimización específica                 | Control total           | Dependencia del SGBD              |
| **Specifications / Criteria** | Filtros dinámicos                       | Reutilizables, potentes | Verbosos                          |
| **JdbcTemplate**              | Bajo nivel                              | Flexibilidad            | Más código, menos abstracción     |
| **QueryDSL**                  | Tipado fuerte, seguridad en compilación | Legible, potente        | Configuración inicial compleja    |
| **Micronaut Data**            | Alternativa ligera                      | Rendimiento alto        | Ecosistema reducido               |

---

## 📚 Referencias

* [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
* [JPQL Guide (Oracle)](https://docs.oracle.com/javaee/7/tutorial/persistence-querylanguage.htm)
* [Baeldung: Spring Data JPA Queries](https://www.baeldung.com/spring-data-derived-queries)
* [Spring Framework: JdbcTemplate](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc-JdbcTemplate)


---
