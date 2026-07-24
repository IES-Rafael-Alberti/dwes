# Seguridad en aplicaciones MVC

Una aplicación MVC segura valida la entrada, escapa la salida, protege las mutaciones, mantiene una sesión controlada y autoriza cada recurso. Ocultar un botón o exigir inicio de sesión NO demuestra que el usuario pueda operar sobre cualquier identificador.

## Controles del núcleo

| Riesgo | Control | Evidencia en el Gestor |
| --- | --- | --- |
| CSRF | Token en toda mutación y métodos POST | Petición sin token responde `403` |
| XSS | Salida con `th:text`; evitar HTML sin escapar | Las plantillas no usan `th:utext` para datos de usuario |
| Entrada inválida | Form object, Jakarta Validation y `BindingResult` | Datos inválidos no se persisten |
| Robo o abuso de sesión | Spring Security, logout POST y cookies gestionadas por el framework | Acceso anónimo redirige al login |
| IDOR/acceso horizontal | Consulta por identificador y propietario | Recurso ajeno e inexistente responden igual |
| Credenciales expuestas | BCrypt y aprovisionamiento local explícito | Perfil normal no crea cuentas |

## CSRF

Un navegador autenticado envía automáticamente su cookie de sesión. Un sitio externo podría intentar provocar una mutación aprovechando esa cookie. El token CSRF vincula el formulario legítimo con la sesión.

- Mantener CSRF activo.
- Usar POST para crear, editar, cambiar estado, borrar y cerrar sesión.
- Renderizar formularios con Thymeleaf y Spring Security.
- Probar tanto la petición con token como la petición sin token.

CSRF no sustituye la autorización: un token válido solo demuestra el origen esperado del formulario, no que el usuario sea propietario del recurso.

## XSS y escape de salida

`th:text` escapa caracteres con significado HTML. `th:utext` inserta HTML sin escapar y no debe recibir datos no confiables. Validar longitud o formato mejora el contrato de entrada, pero no reemplaza el escape contextual de salida.

Las plantillas tampoco deben construir JavaScript, estilos o atributos inseguros mediante concatenación de entrada del usuario.

## Validación del lado servidor

La validación del navegador mejora la experiencia, pero se puede omitir enviando una petición manual. El servidor debe aplicar Jakarta Validation sobre un form object específico y no sobre todos los campos de la entidad.

Una entrada inválida debe:

1. volver al formulario con mensajes claros;
2. conservar los valores seguros introducidos;
3. no ejecutar ninguna mutación.

## Sesiones y autenticación

- No publicar contraseñas ni usuarios predeterminados.
- Codificar contraseñas con un algoritmo adaptativo como BCrypt.
- Mantener el perfil normal sin cuentas de demostración.
- Usar un perfil local explícito y variables de entorno cuando se necesite una demo.
- Cerrar sesión mediante POST protegido por CSRF.

En un despliegue real deben revisarse además HTTPS, atributos `Secure`, `HttpOnly` y `SameSite` de cookies, expiración de sesión y gestión de secretos.

## Autorización por propietario

Cada consulta y mutación debe usar la identidad autenticada junto al identificador del recurso. El Gestor aplica una búsqueda equivalente a `findByIdAndOwnerUsername(id, username)` para leer, editar, alternar y borrar.

Responder del mismo modo ante un recurso inexistente y uno ajeno evita confirmar identificadores válidos. Esta política debe residir en el servicio/repositorio y probarse para TODAS las mutaciones.

## Lista de comprobación

- [ ] No se desactiva CSRF.
- [ ] No hay mutaciones mediante GET.
- [ ] La salida aportada por usuarios se escapa.
- [ ] La validación se ejecuta en el servidor sobre un form object.
- [ ] Los errores no modifican datos.
- [ ] La sesión no depende de credenciales publicadas.
- [ ] Cada operación consulta por recurso y propietario.
- [ ] Un recurso ajeno no revela más información que uno inexistente.
- [ ] Las pruebas cubren acceso anónimo, CSRF, validación y propiedad.

La implementación y sus pruebas se recorren en la guía del [Gestor de tareas](../01-documentacion/06-gestor-tareas-seguro.md); aquí no se duplica su estructura interna.
