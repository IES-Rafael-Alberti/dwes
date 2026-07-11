# Relaciones uno a uno
## Anotación @OneToOne
La anotación @OneToOne existe en Spring Boot y se utiliza en JPA para mapear la entidad fuente con la entidad objetivo³. Esta anotación declara que una relación entre dos entidades es uno a uno⁴.

Por ejemplo, supongamos que estamos construyendo un sistema de gestión de usuarios, y nuestro jefe nos pide que almacenemos una dirección de correo para cada usuario. Un usuario tendrá una dirección de correo, y una dirección de correo tendrá solo un usuario vinculado a ella. Este es un ejemplo de una relación uno a uno, en este caso entre las entidades de usuario y dirección¹.

Aquí tienes un ejemplo de cómo se implementaría esto en JPA:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    //...
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
    // ... getters and setters
}
```

En este ejemplo, colocamos la anotación @OneToOne en el campo de la entidad relacionada, Address. También necesitamos colocar la anotación @JoinColumn para configurar el nombre de la columna en la tabla de usuarios que se mapea a la clave primaria en la tabla de direcciones¹.

(1) Spring Boot + JPA/Hibernate One to One Mapping Example - Java Guides. https://www.javaguides.net/2019/08/spring-boot-jpa-hibernate-one-to-one-mapping-example.html.
(2) One-to-One Relationship in JPA - Spring Framework Guru. https://springframework.guru/one-to-one-relationship-in-jpa/.
(3) One-to-One Relationship in JPA | Baeldung. https://www.baeldung.com/jpa-one-to-one.
(4) What is the real purpose of @OneToOne in Spring boot hibernate?. https://stackoverflow.com/questions/50969106/what-is-the-real-purpose-of-onetoone-in-spring-boot-hibernate.
(5) Hibernate : Difference between @ Embedded annotation technique and .... https://stackoverflow.com/questions/13723914/hibernate-difference-between-embedded-annotation-technique-and-onetoone-ann.
(6) github.com. https://github.com/GHKosovo/DailyNote/tree/1bf29e81baeedd1834c6ca0c3aa072462fc5fe65/Back-end%2FSQL%2Fspring-data-jpa.md.


## ¿Cuándo sería necesaria anotar una relación como @OneToOne?
La anotación @OneToOne es necesaria si quieres establecer una relación uno a uno entre dos entidades en tu base de datos usando JPA en Spring Boot.

Esta anotación ayuda a Hibernate (la implementación más común de JPA) a entender la relación entre las entidades y a generar las consultas SQL adecuadas para manejar estas relaciones.

Si no incluyes la anotación @OneToOne, Hibernate no sabrá que existe una relación uno a uno entre las entidades y no podrá manejar correctamente las operaciones de la base de datos relacionadas con estas entidades.

Por lo tanto, si estás trabajando con relaciones uno a uno en tu base de datos, es recomendable que utilices la anotación @OneToOne. Sin embargo, como con cualquier decisión de diseño, debes considerar cuidadosamente tus necesidades y requisitos específicos antes de decidir utilizarla.

## ¿La ponemos en los dos lados de la relación?
La anotación @OneToOne se puede poner en ambos lados de la relación para crear una relación bidireccional¹². Sin embargo, es importante tener en cuenta que solo un lado de la relación puede ser el propietario. El lado propietario es el que maneja la clave foránea en la base de datos¹.

En el caso de una relación bidireccional, el lado no propietario debe usar el atributo `mappedBy` de la anotación @OneToOne para especificar el campo o propiedad de la relación del lado propietario⁴.

Aquí tienes un ejemplo de cómo se implementaría esto en JPA:

```java
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    //...
    @OneToOne(mappedBy = "address")
    private User user;
    // ... getters and setters
}
```

En este ejemplo, la entidad `Address` es el lado no propietario de la relación, por lo que usamos el atributo `mappedBy` en la anotación @OneToOne¹. Esto le dice a Hibernate que la clave foránea para esta relación se maneja en la entidad `User`, que es el lado propietario de la relación¹.

(1) One-to-One Relationship in JPA | Baeldung. https://www.baeldung.com/jpa-one-to-one.
(2) JPA @JoinColumn annotation with One To One relationship. https://stackoverflow.com/questions/38054178/jpa-joincolumn-annotation-with-one-to-one-relationship.
(3) OneToOne (Java EE 6 ) - Oracle. https://docs.oracle.com/javaee/6/api/javax/persistence/OneToOne.html.
(4) When to use / not to use, @OneToOne and @ManyToOne. https://stackoverflow.com/questions/3263589/when-to-use-not-to-use-onetoone-and-manytoone.
(5) github.com. https://github.com/Adwai-T/SimpleQuiz/tree/841b58d46f48450717fbdf6b9f25410efdec4640/Notes.md.
(6) github.com. https://github.com/GHKosovo/DailyNote/tree/1bf29e81baeedd1834c6ca0c3aa072462fc5fe65/Back-end%2FSQL%2Fspring-data-jpa.md.