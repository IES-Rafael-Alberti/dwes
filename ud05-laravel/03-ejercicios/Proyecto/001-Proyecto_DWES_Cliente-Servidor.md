# Proyecto UD5: API REST con Laravel 12

## Propósito

Diseña y construye una API REST para un dominio elegido y aprobado por el profesorado. La API debe tener usuarios y recursos que pertenezcan a un usuario. No se evalúa una interfaz Blade o un cliente Vue: el foco es el contrato HTTP, la persistencia, la seguridad y las pruebas.

## Entorno obligatorio

- Laravel 12.
- Laravel Sail para PHP, Composer y servicios; no se requiere PHP ni Composer globales.
- Base de datos relacional mediante migraciones.
- Repositorio Git propio según la entrega indicada por el profesorado.

## Requisitos mínimos

1. Define un recurso principal con operaciones de alta, listado paginado, detalle, modificación y borrado.
2. Usa migraciones, modelos Eloquent, factories y seeders para que el entorno pueda reconstruirse.
3. Valida cada entrada HTTP con Form Requests. No uses `$request->all()` ni confíes en validación de cliente.
4. Devuelve un contrato JSON consistente mediante API Resources. Las listas paginadas deben conservar `data`, `links` y `meta`.
5. Implementá registro, inicio y cierre de sesión con tokens de Laravel Sanctum.
6. Protege las mutaciones con autenticación y Policies: una persona no puede leer, modificar ni borrar recursos ajenos salvo una regla explícita de administrador.
7. Separa las reglas de negocio que no pertenezcan al controlador en servicios o acciones con una responsabilidad clara.
8. Devuelve errores de validación, autenticación, autorización y regla de negocio con estados HTTP correctos y un cuerpo JSON documentado.
9. Escribe pruebas Feature y Unit. Deben cubrir al menos autenticación, validación, un CRUD completo, acceso de propietario, rechazo de acceso ajeno y un caso de negocio.
10. Documenta cómo arrancar, reiniciar datos y ejecutar pruebas exclusivamente con Sail.

## Entrega

El `README.md` del repositorio debe incluir:

- Problema que resuelve la API y modelo de recursos.
- Requisitos y comandos de Sail para instalar, levantar, migrar, sembrar y probar.
- Tabla de endpoints con método, ruta, autenticación, entrada y respuesta.
- Decisiones de autorización y regla de negocio.
- Evidencia de `./vendor/bin/sail artisan test` en verde.

Incluye una colección HTTP reproducible o un fichero OpenAPI si se solicita para la entrega. Nunca incluyas `.env`, tokens, claves, bases de datos exportadas ni archivos de `vendor/` o `node_modules/`.

## Rúbrica orientativa

| Evidencia | Peso |
|---|---:|
| Contrato REST, recursos y paginación | 20% |
| Persistencia, migraciones y datos reproducibles | 15% |
| Validación y manejo de errores | 15% |
| Sanctum, Policies y seguridad por propietario | 20% |
| Pruebas automatizadas significativas | 20% |
| README, Sail y calidad de diseño | 10% |

Un proyecto que no se pueda levantar y probar con Sail no puede demostrar su funcionamiento. La autenticación y la autorización por recurso son requisitos mínimos, no ampliaciones opcionales.
