# Inventario de reforma de UD4

## Decisión

UD4 se rehace sobre **PHP 8.4**, TDD y seguridad transversal. GTask será el proyecto conductor, pero su repositorio anidado no se modifica en este corte porque contiene cambios locales (`SEGUIMIENTO.md` y `.env`).

## Clasificación del material recibido

| Grupo | Diagnóstico | Destino |
|---|---|---|
| Apuntes `.org` de fundamentos | Contenido útil, fragmentado y mayoritariamente en inglés | Fusionado en `01-fundamentos-php84.md` |
| Formularios, sesiones y base de datos | Útiles, con repetición en `tmp.org`/`FicheroMarkDown.md` y ejemplos inseguros | Reescritos en los temas 02–04 |
| CRUD y OOP del concesionario | Secuencia didáctica aprovechable, pero código antiguo e inseguro | Conceptos reescritos en los temas 05–06 |
| PDF y TeX | Exportaciones comprobadas frente a los `.org`; no aportan secciones únicas | Retirados |
| `tmp.org`, `*~` y Markdown conversacional | Copias parciales, temporales o respuestas sin editar | Retirados |
| HTML «PHP necesario para Laravel» | Exportación del Markdown homónimo: mismos apartados y ejemplos, sin contenido único | Retirado; fuente Markdown fusionada en tema 07 |
| ZIP del concesionario | Copia del directorio de 2022 | Retirado del recorrido |
| CRUD concesionario 2022 | SQL concatenado, credenciales/código mezclados, validación y protección insuficientes | Archivado; queda un análisis público de deuda |
| Actividad `-profe` y GIFT | Material docente/soluciones | Trasladado a `99-profesor/` local e ignorado |
| GTask heredado | Gitlink con valor histórico y cambios locales | Preservado sin cambios; sustituido en el recorrido por `04-proyectos/gtask-php84` |
| GTask canónico | No existía | Starter público incremental y solución final local en `99-profesor/` |

## Riesgos comprobados

- El código histórico normaliza concatenar entradas en SQL y no incorpora una política sistemática de CSRF, escape y autorización por propietario.
- La coexistencia de `.org`, PDF, TeX, HTML y Markdown hacía imposible identificar la fuente vigente.
- GTask es un repositorio anidado con cambios locales; cualquier actualización automática podría destruir trabajo docente.
- Excluir un archivo de la navegación no lo hace privado: únicamente `99-profesor/`, ignorado por Git y MkDocs, es válido para el material local docente.

## Matriz de conservación de contenido único

| Original local ignorado | Contenido recuperado | Destino público versionado |
|---|---|---|
| `01-PHP_Basics/00-PHP-basics.org` | entorno, tipos, operadores y alcance | `00-entorno-php84.md`, `01-fundamentos-php84.md`, `08-anexo-lenguaje.md` |
| `001-PHP-BasicExercises.org` y `005-PHP-FunctionExercises.org` | veinte contratos: saludo, suma, temperatura, inversión, áreas, variables, franjas horarias, edad, rangos, arrays, factorial, primos, do-while, colores, wrapper HTML, filtro, tabla y variantes de funciones/generadores | `11-ejercicios-fundamentos.md` |
| `01-PHP-Conditionals.org`, `003-PHP-Loops.org` | `match`, while/do-while, break/continue y sintaxis alternativa | `01-fundamentos-php84.md`, `08-anexo-lenguaje.md` |
| `004-PHP-Arrays.org` | multidimensionales y catálogo de operaciones | `08-anexo-lenguaje.md` |
| `005-PHP-functions_1.org`, `_2.org` | tipos, closures, recursión y generadores | `01-fundamentos-php84.md`, `08-anexo-lenguaje.md` |
| `001-PHP-HTML_DataPassing*.org` | PHP embebido, GET/POST y composición | `02-formularios-validacion.md`, `09-php-html-includes.md` |
| `002-PHP-FormValidation.org` | validación y representación segura | `02-formularios-validacion.md` |
| `003-PHP-Sessions_Cookies.org` | ciclo de sesión/cookie y endurecimiento | `03-sesiones-cookies.md`, `06-seguridad/README.md` |
| `004-PHP-Database.org` | PDO, CRUD y transacciones | `04-pdo-persistencia.md`, `05-crud-procedural-seguro.md` |
| `001`–`005-PHP-CARs-CRUD*.org` | CRUD completo, includes y deuda del concesionario | `05-crud-procedural-seguro.md`, `09-php-html-includes.md` |
| `001-PHP-OOP-I.org`, `002-PHP-OOP-CRUD.org` | herencia, interfaces, abstractas, traits, static, capas y significado específico de overloading en PHP | `06-oop-capas.md` |
| `PHP-ToDo*.org` | usuarios, categorías, tareas, autenticación y propiedad | `10-todo-oop.md` |
| `01-PHP_Necesario_Laravel-12.md` | comparación PHP explícito/Laravel | `07-puente-laravel12.md` |

Los `tmp.org`, `FicheroMarkDown.md` y backups `*~` repetían subconjuntos de esas fuentes. Los PDF/TeX eran exportaciones de los mismos títulos y se compararon por estructura. El HTML de Laravel reproducía el Markdown homónimo y no contenía secciones únicas.

### Correspondencia de los veinte ejercicios

| Fuente | Ejercicios preservados |
|---|---|
| `001-PHP-BasicExercises.org` | A1 primer script; A2 variables; A3 saludo condicional; A4 edad; A5 bucles; A6 arrays; A7 factorial; A8 primos; A9 do-while/usuario; A10 switch/match de colores |
| `005-PHP-FunctionExercises.org` | B1 saludo; B2 suma; B3 temperatura; B4 inversión; B5 áreas; B6 factorial como función; B7 primalidad como función; B8 wrapper HTML; B9 filtro; B10 tabla de multiplicar |

No se descarta ninguno. Los dos pares repetidos —factorial y primalidad— se mantienen como progresión script → función, no se fusionan silenciosamente.

### Validación del GIFT privado

Los caracteres reservados `=`, `~`, `#`, `{`, `}` y `:` se escapan dentro de preguntas, respuestas y feedback; únicamente quedan sin escapar los delimitadores estructurales. La validación local comprueba 30 bloques, llaves balanceadas, cuatro alternativas, una sola alternativa correcta, un solo separador de feedback por alternativa y ausencia de reservados sin escapar en el contenido. **No se ha realizado una importación real en Moodle**, por lo que esa comprobación sigue pendiente antes de usar el banco en producción.

## Plan por prioridad

### P0 — saneamiento y contrato documental (completado)

- [x] Fijar PHP 8.4 y una ruta Markdown única.
- [x] Retirar derivados después de fusionar el contenido único y separar material privado.

`90-archivo/` es solo una copia local ignorada: **Git no la preserva ni permite recuperarla tras clonar**. Su existencia no se cuenta como respaldo verificable del repositorio; la enseñanza necesaria está en los Markdown versionados.
- [x] Sacar el concesionario antiguo del recorrido ejecutable.
- [x] Publicar la progresión de seguridad.

### P1 — GTask incremental (completado)

- [x] Preservar el gitlink heredado y documentarlo como fuente histórica/local.
- [x] Definir checkpoints RED → GREEN → REFACTOR activables.
- [x] Crear pruebas de unidad e integración para autenticación, validación, persistencia, propietario y CSRF.
- [x] Crear un entorno reproducible sobre PHP 8.4.

La validación final con PHP 8.4.17 y PHPUnit 11.5.56 ejecuta **20 pruebas y
86 aserciones** sobre validación, autenticación, regeneración de sesión, CSRF,
persistencia, aislamiento por propietario, rutas HTTP, PRG, errores, entrada
hostil y escape XSS. El starter mantiene 2 pruebas base verdes y cinco
checkpoints independientes que producen el RED esperado.

### P2 — práctica procedural segura (completada)

- [x] Crear un starter pequeño, distinto del concesionario heredado.
- [x] Cubrir altas, consultas, cambios y borrado con PDO preparado, PRG y CSRF.
- [x] Publicar contrato del alumnado y conservar solución en `99-profesor/`.

`03-ejercicios/notas-procedural-php84` es el escalón deliberadamente pequeño:
sin login ni capas, con una primera alta modelada por el docente, cuatro
checkpoints RED y una solución final local. Su suite cubre límites de entrada,
inyección tratada como dato, segunda conexión SQLite, CRUD, CSRF, PRG, rutas,
métodos, `404`, XSS y errores genéricos.

La validación final con PHP 8.4.17 y PHPUnit 11.5.56 ejecuta **19 pruebas y
53 aserciones** en la solución. El starter mantiene 2 pruebas base verdes y
cuatro checkpoints independientes con RED observable y sin warnings.

### P3 — cierre y puente a Laravel (completado)

- [x] Comparar las capas de GTask con Request, middleware, controlador, modelo y validación de Laravel 12.
- [x] Consolidar rúbrica y banco GIFT privado.
- [x] Ejecutar la validación completa de código, documentación y seguridad.

La importación real del GIFT en Moodle continúa pendiente como verificación
operativa de plataforma; no bloquea el cierre del contenido y no se declara
realizada.
