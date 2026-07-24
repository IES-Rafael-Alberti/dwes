# Fundamentos de Spring MVC

Spring MVC organiza una aplicación web renderizada en el servidor alrededor de tres responsabilidades: el controlador interpreta HTTP, el modelo contiene los datos necesarios y la vista genera HTML. Spring Boot 4 configura la infraestructura web; no requiere descriptores `web.xml`.

## El problema que resuelve MVC

Mezclar acceso a datos, reglas de negocio y HTML en un único bloque dificulta probar, reutilizar y cambiar la aplicación. MVC establece fronteras reconocibles:

| Pieza | Responsabilidad | No debería hacer |
| --- | --- | --- |
| Modelo | Representar datos y reglas del caso de uso | Conocer HTML o rutas |
| Vista | Presentar el modelo como HTML | Consultar repositorios o decidir permisos |
| Controlador | Adaptar petición, modelo, vista y redirección | Concentrar reglas de negocio |

En una aplicación real, los servicios y repositorios completan estas tres piezas: el controlador delega el caso de uso y el repositorio adapta la persistencia.

## Recorrido de una petición

```text
navegador → DispatcherServlet → controlador → servicio → repositorio
                                             ↓
navegador ← HTML ← motor de plantillas ← modelo + nombre de vista
```

1. El navegador envía una petición HTTP.
2. `DispatcherServlet`, configurado automáticamente, localiza el método controlador.
3. El controlador extrae los datos de entrada y delega el caso de uso.
4. El controlador incorpora al `Model` solo los datos necesarios.
5. Devuelve el nombre lógico de una plantilla.
6. Thymeleaf renderiza HTML y Spring lo entrega al navegador.

## Dependencias mínimas

Para el recorrido MVC de la unidad se utilizan los starters de Spring Boot 4 para Web MVC, Thymeleaf y validación. La persistencia y la seguridad se añaden cuando el caso de uso las necesita.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Spring Boot ya busca plantillas en `src/main/resources/templates/` y recursos estáticos en `src/main/resources/static/`. Solo se modifican esas convenciones cuando existe una necesidad concreta.

## Comprobación

Antes de continuar, se debe poder describir qué componente recibe HTTP, dónde viven las reglas de negocio y qué datos llegan a la plantilla. El siguiente paso es [crear controladores y vistas](02-controladores-y-vistas.md).
