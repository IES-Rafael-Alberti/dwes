# ToDo orientado a objetos

## Modelo

El caso ToDo recupera el contenido útil de usuarios, categorías y tareas sin reproducir clases que mezclaban HTML, SQL y dominio.

```php
final readonly class Task
{
    public function __construct(
        public int $id,
        public int $ownerId,
        public string $title,
        public ?int $categoryId,
        public bool $completed,
    ) {
        if (trim($title) === '') {
            throw new InvalidArgumentException('Title is required');
        }
    }
}
```

`User` representa identidad, `Category` clasifica tareas y `Task` pertenece a un usuario. Las contraseñas se almacenan únicamente como hash:

```php
$hash = password_hash($plainPassword, PASSWORD_DEFAULT);
if (!password_verify($candidate, $hash)) {
    throw new AuthenticationException();
}
```

Nunca se guardan o comparan contraseñas en claro ni se usa MD5/SHA como función de contraseñas.

## Repositorios y casos de uso

```php
interface TaskRepository
{
    public function findAllOwnedBy(int $ownerId): array;
    public function save(Task $task): void;
    public function deleteOwnedBy(int $taskId, int $ownerId): bool;
}
```

La consulta incluye `owner_id`; cargar primero por ID y autorizar después facilita fugas. Los casos de uso coordinan repositorios y transacciones. Las plantillas reciben datos ya preparados y escapan al representar.

## Incrementos previstos

1. registrar y autenticar usuario con hash;
2. crear/listar categorías propias;
3. crear/listar tareas propias;
4. completar y borrar con CSRF;
5. impedir acceso cruzado entre dos usuarios;
6. refactorizar sin cambiar los contratos.

Esta secuencia es un diseño docente. El starter, el harness y sus comandos se entregarán en el corte GTask.
