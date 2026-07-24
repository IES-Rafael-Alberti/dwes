# Laboratorio incremental: Productos MVC

Construye un CRUD de productos con Spring MVC y Thymeleaf siguiendo ciclos cortos **RED → GREEN → REFACTOR**. El starter solo contiene la portada y la configuración: cada etapa añade un comportamiento observable.

## Resultado final

La aplicación permitirá listar, crear, validar, editar y eliminar productos persistidos en H2. Mantendrá separadas las responsabilidades de controlador, servicio y repositorio.

| Componente | Responsabilidad |
| --- | --- |
| `ProductForm` | Binding y validación de la entrada web |
| `Product` | Entidad persistente, sin reglas específicas de la interfaz web |
| `ProductFormMapper` | Traducción explícita entre formulario y entidad |
| `ProductRepository` | Persistencia JPA |
| `ProductService` | Casos de uso y producto inexistente |
| `ProductController` | Rutas MVC, binding y navegación |
| Thymeleaf | Renderizado y errores de formulario |

## Cómo trabajar

En cada etapa:

1. Lee el contrato correspondiente en `checkpoints/`.
2. Convierte ese contrato en un test ejecutable para la capa indicada.
3. Ejecuta `./mvnw test` y conserva la evidencia de que falla por el motivo esperado (**RED**).
4. Implementa solo lo necesario para hacerlo pasar (**GREEN**).
5. Refactoriza sin cambiar el comportamiento y vuelve a ejecutar los tests.

El test de contexto del starter debe estar verde. El rojo aparece al incorporar el contrato de la etapa actual; esto evita que el repositorio parezca roto accidentalmente.

## Etapa 0 — Reconocer el starter

**Demo docente breve:** recorrer el flujo petición → controlador → vista usando únicamente `/`.

**Trabajo del alumnado:** ejecutar tests y aplicación; localizar configuración, clase principal, controlador y plantilla.

**Criterio observable:** `/` responde con la portada y `./mvnw test` termina correctamente.

## Etapa 1 — Listado

**Demo docente breve:** escribir un primer test MVC con `MockMvc` y un servicio sustituido mediante `@MockitoBean`.

**Trabajo del alumnado:** crear la entidad persistente, repositorio, servicio, `GET /products` y `products.html`. La entidad representa datos persistidos y no recibe directamente el formulario web.

**Checkpoint RED:** `GET /products` aún no existe o no entrega el modelo `products` a la vista `products`.

**Criterio observable:** una lista vacía muestra “No hay productos”; una lista con datos genera una fila por producto.

## Etapa 2 — Alta

**Demo docente breve:** explicar el binding de `th:object` y `th:field` sin resolver el formulario completo.

**Trabajo del alumnado:** crear `ProductForm`, implementar `GET /products/new`, el formulario reutilizable y `POST /products`. Usa `ProductForm` como `th:object`; no enlaces la petición directamente a la entidad JPA.

**Checkpoint RED:** un POST válido no invoca todavía el caso de uso de creación.

**Criterio observable:** guardar un producto válido redirige a `/products` y aparece en el listado.

## Etapa 3 — Validación

**Demo docente breve:** provocar un error con `@Valid` y observar `BindingResult`.

**Trabajo del alumnado:** declarar en `ProductForm` las restricciones de entrada —nombre no vacío y precio obligatorio mayor o igual que cero— y representar los errores junto a cada campo. Las reglas de binding pertenecen al formulario; una regla de dominio independiente de la web pertenecería al dominio.

**Checkpoint RED:** un nombre en blanco o un precio negativo se persisten o no generan errores de campo.

**Criterio observable:** el formulario conserva los datos, muestra errores y el servicio no guarda la entrada inválida. No basta con validación HTML: debe validarse en servidor.

## Etapa 4 — Edición

**Demo docente breve:** distinguir el identificador de la URL del contenido recibido por binding.

**Trabajo del alumnado:** implementar `GET /products/{id}/edit` y `POST /products/{id}`. Añade un mapper explícito `ProductForm` ↔ `Product`. El servicio carga la entidad existente y modifica sus campos; no confía en un identificador enviado en el cuerpo.

**Checkpoints RED:** el controlador no usa `ProductForm`, el mapeo pierde campos, se ignora el `id` de ruta o el servicio confía en el identificador recibido. Activa los contratos de controlador, mapper y servicio de esta etapa.

**Criterio observable:** editar conserva el identificador y modifica exactamente el producto indicado.

## Etapa 5 — Eliminación

**Demo docente breve:** razonar por qué una operación destructiva no debe ser un enlace GET.

**Trabajo del alumnado:** añadir `POST /products/{id}/delete` y su formulario Thymeleaf.

**Checkpoint RED:** la petición no elimina mediante el servicio o no redirige.

**Criterio observable:** el producto desaparece y refrescar la página no repite la operación.

## Etapa 6 — Persistencia

**Demo docente breve:** comparar un test unitario con `@DataJpaTest`.

**Trabajo del alumnado:** verificar generación de identificador y precisión monetaria con `BigDecimal`; configurar H2 y JPA para recrear el esquema al arrancar.

**Checkpoint RED:** el repositorio no persiste o el precio pierde su valor decimal exacto.

**Criterio observable:** el producto se recupera por su identificador durante la ejecución y todos los tests quedan verdes.

## Contrato HTTP final

| Método | Ruta | Resultado |
| --- | --- | --- |
| GET | `/products` | Listado |
| GET | `/products/new` | Formulario vacío |
| POST | `/products` | Crear o volver al formulario con errores |
| GET | `/products/{id}/edit` | Formulario con datos |
| POST | `/products/{id}` | Actualizar o volver al formulario con errores |
| POST | `/products/{id}/delete` | Eliminar |

## Criterios de evaluación

| Área | Peso | Evidencia |
| --- | ---: | --- |
| Arquitectura MVC | 35 % | Responsabilidades separadas y dependencias por constructor |
| TDD y pruebas | 25 % | Evidencias RED/GREEN y contratos suministrados de controlador, `ProductForm`, mapper, actualización en servicio y persistencia JPA |
| Formularios y Thymeleaf | 20 % | Binding, navegación, mensajes y reutilización del formulario |
| Validación y persistencia | 20 % | Restricciones en servidor, `BigDecimal`, JPA y H2 |

## Entrega

Incluye el proyecto y un README propio con requisitos, comandos de ejecución y decisiones relevantes. Antes de entregar:

- [ ] `./mvnw test` está verde.
- [ ] No hay plantillas Mustache ni código de solución ajeno.
- [ ] Las operaciones destructivas usan POST.
- [ ] Los errores se muestran sin perder los datos introducidos.
- [ ] El controlador enlaza `ProductForm`, nunca directamente la entidad JPA.
- [ ] Los commits representan incrementos funcionales, no capas aisladas.

## Ampliaciones opcionales

Solo después de completar el núcleo: detalles de producto, búsqueda por nombre, paginación, fragmentos Thymeleaf o una base de datos externa. Autenticación y roles quedan fuera de esta práctica.
