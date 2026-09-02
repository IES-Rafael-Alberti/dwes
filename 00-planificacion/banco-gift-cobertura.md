# Banco GIFT y matriz de cobertura — DWES 2026/2027

## Estado

Banco preparado durante la fase 4 del rediseño para 30 alumnos. Se han creado
**17 bancos nuevos** con **16 preguntas cada uno: 272 preguntas nuevas**.

La sintaxis ha sido revisada estructuralmente y los ficheros se han comprobado
como texto UTF-8. **La importación y previsualización real en Moodle siguen
pendientes**; no se debe considerar validado el banco hasta ejecutar esa prueba.

Los ficheros `.gift` están excluidos por `**/*.gift` en `.gitignore` y no forman
parte de MkDocs ni de GitHub Pages. Esta matriz no contiene respuestas.

## Convenciones

- Cada cuestionario contiene 16 preguntas para facilitar intentos de 15-20
  preguntas con selección aleatoria cuando Moodle disponga de un banco mayor.
- `SU`: selección única.
- `SM`: selección múltiple.
- `VF`: verdadero/falso.
- `EM`: emparejamiento.
- `HC`: hueco de código con alternativas cerradas.
- `RC`: respuesta corta; solo debe conservarse tras comprobar que es inequívoca.
- La etiqueta RA/CE es una asignación inicial de cobertura y debe contrastarse
  con la rúbrica normativa antes de ponderar cuestionarios.

## Bancos nuevos

| Banco | Unidad/semana | Tema | Preguntas | Tipos previstos | RA/CE principal |
|---|---|---|---:|---|---|
| `ud01-2026-semana-01-http.gift` | UD1 / 1 | Métodos, mensajes, estados, cabeceras y cookies | 16 | SU, SM, VF, EM, HC | RA1.a-g |
| `ud01-2026-semana-02-entorno-tdd.gift` | UD1 / 2 | Entorno servidor, Spring inicial y TDD | 16 | SU, SM, VF, EM, HC | RA1.a-g |
| `ud02a-2026-semana-03-spring-tdd.gift` | UD2a / 3 | Spring Boot, DI, Maven, TDD y slicing | 16 | SU, SM, VF, EM, HC | RA5.a-b |
| `ud02a-2026-semana-04-rest-controladores.gift` | UD2a / 4 | REST, controladores, DTO y `ResponseEntity` | 16 | SU, SM, VF, EM, HC | RA5.b, RA5.g |
| `ud02a-2026-semana-05-persistencia.gift` | UD2a / 5 | JPA, repositorios, transacciones y paginación | 16 | SU, SM, VF, EM, HC | RA5.h, RA6.a-g |
| `ud02a-2026-semana-06-seguridad-api.gift` | UD2a / 6 | Validación, JWT, CORS, 401/403 y secretos | 16 | SU, SM, VF, EM, HC | RA7.a-h |
| `ud02a-2026-semana-07-errores-pruebas.gift` | UD2a / 7 | Errores, advice, MockMvc y pruebas de contrato | 16 | SU, SM, VF, HC | RA5.b, RA5.h, RA7.h |
| `ud02a-2026-semana-07-openapi-contratos.gift` | UD2a / 7 | OpenAPI, esquemas, ejemplos y seguridad del contrato | 16 | SU, SM, VF, EM, HC | RA5.h, RA7.a-h |
| `ud03-2026-semana-08-mvc-thymeleaf.gift` | UD3 / 8 | MVC, Thymeleaf, formularios y PRG | 16 | SU, SM, VF, EM, HC | RA5, RA8 |
| `ud03-2026-semana-09-sesion-seguridad.gift` | UD3 / 9 | Sesiones, CSRF, XSS, auth y propietario | 16 | SU, SM, VF, EM, HC, RC | RA4, RA8 |
| `ud04-2026-semana-10-php-seguro.gift` | UD4 / 10 | PHP 8.4, formularios, sesiones y escape | 16 | SU, SM, VF, HC | RA2, RA3, RA4 |
| `ud04-2026-semana-11-pdo-oop.gift` | UD4 / 11 | PDO, SQL injection, transacciones, OOP y tests | 16 | SU, SM, VF, EM, HC | RA3, RA5, RA6 |
| `ud05-2026-semana-12-laravel-api.gift` | UD5 / 12 | Rutas, middleware, requests y resources Laravel | 16 | SU, SM, VF, EM, HC | RA5, RA6 |
| `ud05-2026-semana-13-eloquent-seguridad.gift` | UD5 / 13 | Eloquent, migraciones, Sanctum y policies | 16 | SU, SM, VF, EM, HC | RA4, RA5, RA6 |
| `ud00-2026-repaso-transversal-servidor.gift` | Repaso / 14 | HTTP, capas, TDD, persistencia y seguridad | 16 | SU, SM, VF, EM, HC | RA1-RA8 |
| `ud06-2026-semana-14-integracion.gift` | UD6 / 14 | APIs externas, mapeo, idempotencia y offline | 16 | SU, SM, VF, EM, HC | RA9.a-h |
| `ud06-2026-semana-15-seguridad-resiliencia.gift` | UD6 / 15 | Procedencia, claves, CORS, rate limit y resiliencia | 16 | SU, SM, VF, EM, HC | RA9.c-h |

## Bancos existentes conservados

| Banco | Preguntas registradas | Acción en fase 4 |
|---|---:|---|
| `ud01_repaso.gift` | 16 | Conservar como banco anterior; ampliar en una revisión posterior si hace falta |
| `ud02a_semanas_1_4.gift` | 12 | Conservar; complementar con los bancos nuevos |
| `ud02a_semanas_5_8.gift` | 8 | Conservar; complementar y revisar en Moodle |
| `ud03-mvc-thymeleaf-seguridad.gift` | 12 | Conservar; complementar con los dos bancos UD3 nuevos |
| `ud06_ra9.gift` | 14 | Conservar como banco privado; comprobar importación y cobertura |
| `ud04/99-profesor/cuestionarios/php-basico.gift` | 30 | Conservar como material docente previo; no mezclar automáticamente |

## Cobertura temática transversal

| Tema | Bancos que lo cubren | Evidencia de pregunta |
|---|---|---|
| Métodos, estados y cabeceras HTTP | UD1-01, UD00 | Escenario y fragmento de mensaje |
| TDD y test slicing | UD1-02, UD2a-03, UD2a-07 | Orden del ciclo, aislamiento y elección de test |
| Controlador-servicio-repositorio | UD2a-03/04/05, UD04-11, UD05-12 | Responsabilidad y código con hueco |
| Validación y errores | UD2a-04/06/07, UD03-08/09, UD04-10, UD05-12/13 | Caso inválido, respuesta y mitigación |
| Persistencia y transacciones | UD2a-05, UD04-11, UD05-13, UD00 | Decisión de integridad y fragmento cerrado |
| Seguridad de servidor | UD1-01, UD2a-06, UD03-09, UD04-10/11, UD05-13, UD06-15 | Amenaza-mitigación y escenarios |
| API/OpenAPI/integración | UD2a-04/07, UD06-14/15, UD00 | Contrato, payload, timeout y error |
| PHP/Laravel | UD04-10/11, UD05-12/13, UD00 | Sintaxis aplicada, ORM y seguridad |

## Control de calidad

- [x] Todos los bancos nuevos tienen 16 identificadores de pregunta.
- [x] Hay 272 preguntas nuevas, por encima del objetivo mínimo de 250.
- [x] Los bancos nuevos usan nombres únicos y comentarios temáticos.
- [x] Los bancos históricos no se han sobrescrito.
- [x] Los `.gift` están excluidos por `.gitignore`.
- [ ] Importar un banco piloto de UD1 en Moodle.
- [ ] Importar un banco piloto de UD2a en Moodle.
- [ ] Previsualizar preguntas, escapes, código, emparejamientos y puntuación.
- [ ] Confirmar en Moodle los tipos realmente soportados por la instalación.
- [ ] Registrar incidencias de importación y correcciones aplicadas.
- [ ] Marcar cada banco como validado solo después de esa comprobación.

## Incidencias conocidas

No se ha ejecutado una importación real porque no hay acceso a una instancia de
Moodle desde este repositorio. La fase 4 queda preparada, pero la validación
operativa es un paso docente pendiente.
