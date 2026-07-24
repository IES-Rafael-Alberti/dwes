# Gestor de tareas seguro con Spring MVC

Este ejemplo integrador aplica lo aprendido en Productos a una aplicación con sesión: cada usuario solo puede consultar y modificar sus propias tareas. El proyecto canónico está en `02-ejemplos/SpringMVC/`.

## Ruta rápida

1. Ejecuta `GRADLE_USER_HOME=/tmp/gradle-ud3 ./gradlew test` dentro del proyecto.
2. Lee primero `TaskSecurityTests`: los tests expresan el contrato de seguridad.
3. Recorre las etapas siguientes en orden; no copies el proyecto entero antes de entender cada frontera.

## Etapas

### 1. Dominio y persistencia

`User` y `Task` son entidades. Una tarea tiene un propietario obligatorio. `TaskRepository` no expone búsquedas globales a los casos de uso: usa siempre el nombre del propietario en la consulta.

**Comprobación:** el listado de Ana no contiene tareas de Bob.

### 2. Form object y validación

`TaskForm` es independiente de la entidad y contiene las reglas de entrada con Jakarta Validation. El controlador valida; el servicio recibe datos ya válidos y aplica el caso de uso.

**Comprobación:** una descripción en blanco vuelve al formulario y no persiste nada.

### 3. Casos de uso

`TaskService` concentra listar, crear, consultar, editar, cambiar estado y borrar. `getOwned` busca simultáneamente por identificador y propietario. Así, un recurso inexistente y uno ajeno producen el mismo `404` y no filtran información.

**Comprobación:** Ana no puede leer, modificar, alternar ni borrar una tarea de Bob.

### 4. MVC con Thymeleaf

`TaskController` solo coordina HTTP, validación, modelo y redirecciones. Las plantillas Thymeleaf usan formularios POST para todas las mutaciones; editar, alternar, borrar y cerrar sesión nunca son enlaces GET.

### 5. Seguridad de sesión y CSRF

Spring Security mantiene CSRF activo. Thymeleaf añade el token a los formularios POST integrados con Spring. Una petición mutante sin token recibe `403`.

La guía transversal [`../06-seguridad/README.md`](../06-seguridad/README.md) explica el modelo de amenazas y la lista de comprobación. Este documento se limita a señalar cómo lo demuestra el Gestor.

No hay usuarios ni contraseñas precargados, impresos o publicados. Los tests crean sus propios usuarios. El aprovisionamiento y el registro público quedan para un corte posterior, donde deberán tener DTO, validación, contraseña codificada y prueba específica.

## Mapa de responsabilidades

| Pieza | Responsabilidad |
| --- | --- |
| `TaskForm` | Entrada y validación del formulario |
| `TaskController` | Adaptación HTTP/MVC |
| `TaskService` | Casos de uso y frontera de propietario |
| `TaskRepository` | Consultas acotadas por propietario |
| `SecurityConfig` | Autenticación de formulario, autorización y CSRF |
| `TaskSecurityTests` | Contrato ejecutable de negocio y seguridad |

## Qué queda fuera de este corte

- Registro y recuperación de credenciales.
- Roles administrativos.
- Búsqueda, filtros, paginación y acciones en lote.
- API REST, AJAX y notificaciones.
- Base de datos persistente y migraciones Flyway.

Estas extensiones solo se añaden cuando preservan la consulta por propietario y amplían primero el contrato de tests.

## Demostración desde un clon limpio

El perfil normal no crea cuentas. Para una demostración local reproducible usa el perfil explícito `demo` y aporta las credenciales mediante el entorno:

```bash
DEMO_USERNAME=teacher-demo \
DEMO_PASSWORD='choose-a-local-password' \
SPRING_PROFILES_ACTIVE=demo \
GRADLE_USER_HOME=/tmp/gradle-ud3 \
./gradlew bootRun
```

El aprovisionador solo existe en ese perfil, guarda BCrypt, no muestra la contraseña y no duplica el usuario si ya existe. Si falta una variable, el arranque falla en vez de usar credenciales inseguras. La consola H2 continúa deshabilitada.
