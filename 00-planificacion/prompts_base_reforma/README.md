# Prompts base para la reforma de DWES

Esta carpeta conserva una copia de los prompts usados como arranque de la reforma de **Desarrollo Web en Entorno Servidor (DWES)**, adaptados a partir de los prompts base creados originalmente para **Sistemas de Big Data**.

## Prompts semilla

1. `00_prompt_inventario_material_modulo_BASE.md`
   - Define el criterio general de inventario.
   - Sirve como contrato base para decidir qué catalogar y qué excluir.

2. `01_prompt_inventario_p1_barrido_BASE.md`
   - Ejecuta el primer barrido bruto del material.
   - Produce un inventario provisional revisable.

3. `02_prompt_inventario_p2_consolidacion_BASE.md`
   - Consolida el inventario bruto.
   - Genera el inventario final que debe guiar la reorganización.

4. `03_prompt_comparacion_seleccion_multicurso_BASE.md`
   - Compara materiales de varios cursos antes de reorganizar.
   - No incluido en esta carpeta. Si hay versiones históricas de varios cursos, adaptar desde el equivalente de SBD.

5. `04_prompt_reorganizacion_material_BASE.md`
   - Prompt de reorganización adaptado a DWES.
   - Unidades propuestas: ud01-introduccion, ud02-api-rest (subunidades spring-boot, dotnet, graphql), ud03-mvc, ud04-php, ud05-laravel, ud06-hibridas, ud07-proyecto.

## Material a inventariar

El material original del curso actual está en:

`../../Unidades/`

Estructura actual detectada:

| Directorio | Contenido |
|------------|-----------|
| `U1/` | Introducción, HTTP, instalación primer contacto |
| `U2a_ApiRest_SpBoot/` | API REST con Spring Boot (Java/Kotlin) |
| `U2b_DotNetApiREST/` | API REST con .NET/C# |
| `U2c-GraphQL-HotChocolate-dotNet/` | GraphQL con HotChocolate (.NET) |
| `U3_SpBootMVC/` | Spring Boot MVC (Thymeleaf, Mustache) |
| `U4_PHP/` | PHP básico, avanzado, CRUD, OOP |
| `U5_Laravel/` | Laravel (API, proyecto, sail) |
| `U6/` | Aplicaciones híbridas |
| `Java/` | Materiales Java (geonotes, calc21, kotlin→java) |
| `Examenes/` | Exámenes, rúbricas, checklists |
| `ProyectoConjunto/` | Proyecto intermodular |
| `DWES/Unidades/UD2/` | Estructura anidada de curso anterior |

## Qué adaptar si se reusa en otro módulo

- Nombre completo del módulo.
- Código corto del módulo.
- Número y nombre de unidades.
- Ruta origen y ruta destino.
- Tecnologías propias del módulo.
- Criterios de exclusión específicos.

## Flujo recomendado

1. Ejecutar inventario base (`00`).
2. Ejecutar primer barrido (`01`).
3. Consolidar inventario (`02`).
4. Si hay varias versiones históricas útiles, comparar antes de reorganizar.
5. Con el inventario final, adaptar el prompt de reorganización (`04`) y ejecutarlo.
6. Tras reorganizar, crear un documento vivo de estado y pendientes.

## Tecnologías DWES a tener en cuenta en el inventario

- **Backend**: Java (17-21), Kotlin, Spring Boot 3.x/4.x, .NET/C#, PHP 8.x, Laravel 11/12
- **Bases de datos**: PostgreSQL, MySQL, MongoDB, H2 (dev), Redis
- **Frontend integrado**: Thymeleaf, Mustache, Blade (Laravel)
- **APIs**: REST, GraphQL, JWT, OAuth2
- **Infra**: Docker, Docker Compose, Laravel Sail, Gradle, Maven
- **Testing**: JUnit, Spring Test, PHPUnit, Pest, Postman/Insomnia
- **Herramientas**: IntelliJ IDEA, VS Code, Git, Insomnia, DBeaver
