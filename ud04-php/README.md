# UD4 — PHP 8.4 en entorno servidor

Esta unidad construye una aplicación web segura sin framework para comprender qué resuelve después Laravel. **PHP 8.4 es la versión de referencia** y Markdown es la fuente documental pública.

## Recorrido recomendado

| Paso | Resultado | Material |
|---|---|---|
| 0 | Levantar y diagnosticar PHP 8.4 | [Entorno reproducible](01-documentacion/00-entorno-php84.md) |
| 1 | Dominar sintaxis, tipos, control de flujo, arrays y funciones | [Fundamentos](01-documentacion/01-fundamentos-php84.md) y [anexo de lenguaje](01-documentacion/08-anexo-lenguaje.md) |
| 1b | Practicar contratos sin recibir soluciones | [Banco de 20 ejercicios](01-documentacion/11-ejercicios-fundamentos.md) |
| 2 | Recibir y validar datos HTTP sin confiar en el cliente | [Formularios y validación](01-documentacion/02-formularios-validacion.md) |
| 3 | Mantener estado de forma segura | [Sesiones y cookies](01-documentacion/03-sesiones-cookies.md) |
| 4 | Persistir mediante consultas preparadas | [PDO y persistencia](01-documentacion/04-pdo-persistencia.md) |
| 5 | Componer PHP y HTML y completar un CRUD procedural seguro | [PHP y HTML](01-documentacion/09-php-html-includes.md), [CRUD seguro](01-documentacion/05-crud-procedural-seguro.md) y [práctica Notes](03-ejercicios/notas-procedural-php84/README.md) |
| 6 | Separar dominio, acceso a datos y entrega HTTP | [OOP y capas](01-documentacion/06-oop-capas.md) y [ToDo OOP](01-documentacion/10-todo-oop.md) |
| 7 | Integrar lo aprendido de forma incremental | [GTask canónico](04-proyectos/gtask-php84/README.md) |
| 8 | Reconocer las abstracciones del framework | [Puente a Laravel 12](01-documentacion/07-puente-laravel12.md) |

La [guía de seguridad](06-seguridad/README.md) se aplica desde el primer formulario; no es un tema final aislado.

## Forma de trabajo

La unidad sigue RED → GREEN → REFACTOR. El starter público de GTask entrega el entorno y contratos activables; la única solución final se conserva localmente en `99-profesor/`. No se considera terminado un CRUD porque funcione manualmente: debe validar, escapar, autorizar y conservar pruebas automatizadas.

## Mapa RA/CE

Se usa la numeración de la rúbrica común del módulo (`00-planificacion/rubrica_comun_DWES_por_RA_CE.md`); cada bloque aporta evidencias parciales, no certifica por sí solo un RA completo.

| Bloque | RA y CE vinculados | Evidencia prevista |
|---|---|---|
| Entorno PHP 8.4 | RA1 (CE a–g) | entorno y despliegue justificados |
| Fundamentos y PHP+HTML | RA2 (CE a–h), RA3 (CE a–g) | sintaxis, tipos, alcance, estructuras embebidas y colecciones |
| Formularios, sesiones y seguridad | RA3 (CE a–g), RA4 (CE a–f) | validación, estado, autenticación, CSRF y contraseñas |
| PDO y CRUD | RA6 (CE a–g) | consultas parametrizadas, transacciones e integridad |
| OOP, capas y GTask | RA5 (CE a–h) | entrega, aplicación, dominio e infraestructura separados; TDD |

Los rangos de letras se citan porque la rúbrica común agrupa así los CE; la actividad GTask deberá concretar cuáles evalúa realmente cuando exista su starter.

## Estado de la reforma

- Secuencia documental y baseline PHP 8.4: preparada.
- GTask incremental: starter público y solución docente local preparados para PHP 8.4.
- Práctica procedural Notes: starter incremental público y solución final docente preparados.
- Material histórico del concesionario: retirado del recorrido; se conserva únicamente como caso de análisis de deuda.
- Soluciones y cuestionarios: locales en `99-profesor/`, excluidos de Git y MkDocs.
- Banco GIFT: validado estructuralmente; la importación real en Moodle sigue como comprobación operativa pendiente.
