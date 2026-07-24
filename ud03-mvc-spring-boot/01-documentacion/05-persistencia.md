# Persistencia en una aplicación MVC

La persistencia se incorpora después de dominar controladores, vistas y validación. El controlador no accede directamente a JPA: delega en un servicio, que mantiene el caso de uso y usa un repositorio.

## Flujo de responsabilidades

```text
ProductForm → ProductController → ProductService → ProductRepository → Product
```

| Componente | Responsabilidad |
| --- | --- |
| Form object | Recibir y validar campos permitidos |
| Controlador | Adaptar HTTP, modelo, vistas y redirecciones |
| Servicio | Ejecutar el caso de uso y definir la transacción |
| Repositorio | Consultar y persistir entidades |
| Entidad | Representar identidad, estado persistente y reglas propias |

## Dependencias y configuración

Spring Data JPA y el controlador de la base de datos se añaden al proyecto cuando comienza esta etapa. Para ejercicios se puede usar H2; las credenciales y la consola son locales y no deben convertirse en configuración de producción.

```properties
spring.datasource.url=jdbc:h2:mem:products
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=false
```

`create-drop` es apropiado para una práctica efímera, no para conservar datos reales. Un proyecto persistente requiere migraciones versionadas.

## Operaciones completas

El repositorio puede ofrecer operaciones CRUD, pero el servicio expresa intenciones del dominio: `create`, `update`, `delete` o `getRequired`. La ausencia de una entidad se traduce de forma consistente; no se propagan valores nulos hasta la vista.

En recursos asociados a un usuario, la consulta debe incluir la identidad del propietario:

```java
Optional<Task> findByIdAndOwnerUsername(Long id, String username);
```

Buscar por identificador y comprobar después el propietario facilita errores y fugas de información. La frontera de propiedad debe formar parte de la consulta o del caso de uso atómico.

## Integridad y pruebas

- Validar antes de persistir y conservar restricciones en base de datos.
- Probar el repositorio solo cuando existe una consulta propia relevante.
- Probar el servicio para reglas y recursos inexistentes.
- Probar el controlador para binding, validación, vista y redirección.
- No depender de datos precargados compartidos entre pruebas.

El [Gestor de tareas seguro](06-gestor-tareas-seguro.md) muestra estas fronteras con sesión y propiedad del recurso.
