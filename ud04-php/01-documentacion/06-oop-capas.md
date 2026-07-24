# OOP y separación por capas

## Problema

Cuando cada script valida, consulta, autoriza y genera HTML, cualquier cambio obliga a tocarlo todo. Las clases sirven para expresar contratos y dependencias; no para esconder SQL dentro de una entidad.

## Capas propuestas

| Capa | Responsabilidad |
|---|---|
| Entrega HTTP | Traducir petición y respuesta |
| Aplicación | Coordinar un caso de uso |
| Dominio | Representar reglas y conceptos |
| Infraestructura | Implementar persistencia y servicios externos |

```php
final readonly class CreateTask
{
    public function __construct(private TaskRepository $tasks) {}

    public function execute(int $ownerId, string $title): Task
    {
        $task = Task::create($ownerId, $title);
        $this->tasks->save($task);
        return $task;
    }
}
```

`CreateTask` no conoce `$_POST`, HTML ni PDO. El repositorio es un contrato; su implementación PDO vive en infraestructura.

## Principios

- inyectar dependencias en vez de crearlas dentro de cada método;
- mantener entidades independientes del transporte y la base de datos;
- autorizar con la identidad de sesión, no con un propietario enviado por el cliente;
- probar reglas con dobles pequeños y la persistencia con pruebas de integración.

## Herramientas OOP que sí aportan diseño

```php
interface Clock { public function now(): DateTimeImmutable; }

abstract class AuthenticatedAction
{
    final public function run(Session $session): Response
    {
        return $this->handle($session->requiredUserId());
    }
    abstract protected function handle(int $userId): Response;
}

final class SystemClock implements Clock
{
    public function now(): DateTimeImmutable { return new DateTimeImmutable(); }
}
```

- **Herencia**: relación «es un» estable; no se usa solo para reutilizar líneas.
- **Clase abstracta**: comparte un algoritmo y obliga a completar pasos.
- **Interfaz**: contrato intercambiable, útil para repositorios y servicios.
- **Trait**: reutilización horizontal pequeña; no debe ocultar dependencias o estado global.
- **`static`**: apropiado para constructores con nombre o funciones puras; evitá estado global estático.
- **`final`**: declara que una clase o método no está diseñado para extensión.

Composición e inyección de dependencias son la opción por defecto. Las clases anónimas pueden servir como dobles locales en pruebas, no como arquitectura principal.

### «Sobrecarga» en PHP

PHP **no soporta sobrecarga de métodos por firma** como Java: no se pueden declarar en una clase varios métodos `save` diferenciados solo por cantidad o tipo de parámetros. Las alternativas explícitas son:

- parámetros opcionales cuando existe un valor predeterminado natural;
- parámetros variádicos para una cantidad realmente variable de argumentos homogéneos;
- constructores o métodos con nombres distintos para intenciones diferentes;
- objetos de entrada cuando crece el conjunto de datos.

PHP llama *overloading* a los métodos mágicos `__get`, `__set`, `__isset`, `__unset`, `__call` y `__callStatic`, que interceptan acceso a propiedades o métodos inaccesibles/inexistentes. Ese mecanismo dinámico no equivale a sobrecarga por firma, reduce el análisis estático y no debe ser la opción por defecto del diseño docente.

## Siguiente paso

[ToDo orientado a objetos](10-todo-oop.md) y después [GTask incremental](../03-ejercicios/ActividadesCls/GestionTareas/README.md).
