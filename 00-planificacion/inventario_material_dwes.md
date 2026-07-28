# Inventario del módulo: Desarrollo Web en Entorno Servidor (DWES)

## 1. Resumen ejecutivo

**Material predominante**: Apuntes en formato editable (R Markdown, org-mode, LaTeX, Markdown) con sus derivados publicados (PDF, HTML). Gran cantidad de proyectos base y plantillas, especialmente en Spring Boot y Laravel. PHP domina en volumen de ejercicios estructurados.

**Partes bien cubiertas**:
- **U1 (Introducción)**: Reformada con recorrido Markdown RA1.a-g, Java 25/Spring Boot 4, Hello Server probado y evaluación reproducible.
- **U2a (Spring Boot REST)**: Muy completa: apuntes, anexos, proyectos base (Battleship, GestionEventos, mini-spring-boot-tasks), seguridad JWT, testing, Postman/Insomnia.
- **U4 (PHP)**: Progresión pedagógica clara: básico → avanzado → CRUD → OOP → OOP-CRUD. Formato org-mode + LaTeX de alta calidad.
- **Java/Previos**: Guión de clase Java 17-21 para estudiantes de Kotlin excepcionalmente bueno (433 líneas, 6+ sesiones). Ideal para 00-unidad-0-previos.

**Partes dispersas, duplicadas o antiguas**:
- **U5 (Laravel)**: Contiene una copia del curso 24/25 dentro (`laravel-24_25/`) que duplica toda la estructura. Proyectos en varias versiones de Laravel (10, 11, 12).
- **Battleship (U2a)**: 15 zips de versiones del proyecto en `ProyectoGuia/` + 11 zips históricos en `versiones/`. Necesita limpieza.
- **GeoNotes (Java)**: 8 variantes zip del mismo proyecto. Consolidar a 1-2 versiones.
- **DWES/Unidades/UD2/**: Estructura anidada huérfana del curso anterior.
- **Documentación Laravel dentro de U2a**: Probablemente fuera de sitio.

**Unidades infradotadas**:
- **U2c (GraphQL)**: 1 solo documento monográfico. Podría fusionarse con U2b o ampliarse.
- **U6 (Híbridas)**: 1 documento conceptual. Muy ligera.

**Elementos que conviene revisar antes del próximo curso**:
- Decidir estructura definitiva de unidades (especialmente si U2a/b/c se unifican).
- Depurar versionado de Battleship y GeoNotes.
- Decidir qué hacer con la copia de Laravel 24/25.
- Evaluar si Spring Boot 4 + GraalVM entra en el curso.
- Separar/archivar material de corrección y entregas de alumnado.

## 2. Catálogo de materiales docentes

### U1 — Introducción al desarrollo en entorno servidor

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| `ud01-introduccion-entorno-servidor/` | Recorrido canónico UD1 | Documentación, ejemplo y evaluación | U1 | RA1.a-g, HTTP actual, seguridad, Java 25/Spring Boot 4 y Hello Server | Markdown, Java, Maven | Reformado | Rmd, PDF y plan efímero retirados el 23 julio 2026 |

### U2a — API REST con Spring Boot

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U2a_ApiRest_SpBoot/00-BattleShip.md | 00-BattleShip.md | Enunciado de práctica | U2a | Proyecto Battleship (hundir la flota) | Spring Boot | Vigente | Proyecto principal |
| U2a_ApiRest_SpBoot/03-bis-UD2_Cap3-Vistas-Thymeleaf.md | 03-bis-UD2_Cap3-Vistas-Thymeleaf.md | Apunte | U2a | Vistas con Thymeleaf | Spring Boot, Thymeleaf | Vigente | |
| U2a_ApiRest_SpBoot/Documentacion/SpringBoot/ | SpringBoot/ | Documentación | U2a | Documentación adicional Spring Boot | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/00-Utils_ChuletaGIT.md | ChuletaGIT.md | Guía/Chuleta | Transversal | Chuleta de comandos GIT | Git | Vigente | Recurso transversal |
| U2a_ApiRest_SpBoot/Documentos/01-UD2_Cap3-SpngBoot-Intro.md | SpngBoot-Intro.md | Apunte | U2a | Introducción a Spring Boot | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/02_03b-UD2Cap02bis-Cap03SpringBootControladores.md | SpringBootControladores.md | Apunte | U2a | Controladores en Spring Boot | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/02-Cap02bis_Tests_Tasks_Errores.md | Tests_Tasks_Errores.md | Apunte | U2a | Tests, tareas, manejo de errores | Spring Boot, JUnit | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/02-Cap02bis_Tests_Tasks_Slicing.md | Tests_Tasks_Slicing.md | Apunte | U2a | Test slicing en Spring Boot | Spring Boot, JUnit | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/02-Cap02bis_Tests_Tasks.md | Tests_Tasks.md | Apunte | U2a | Tests y tareas | Spring Boot, JUnit | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/02-UD2_Cap3-Controladores_y_Vistas.md | Controladores_y_Vistas.md | Apunte | U2a | Controladores y vistas | Spring Boot, Thymeleaf | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/03-UD2_Cap4-LaCapaDeservicios.md | LaCapaDeservicios.md | Apunte | U2a | Capa de servicios | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/04-UD2-Anexo_Anotaciones_SpringBoot.md | Anotaciones_SpringBoot.md | Apunte/Anexo | U2a | Referencia de anotaciones | Spring Boot | Vigente | Recurso de referencia |
| U2a_ApiRest_SpBoot/Documentos/05-UD2_Anexo_Hibernate_Records_Avanzado.md | Hibernate_Records_Avanzado.md | Apunte/Anexo | U2a | Hibernate, records, temas avanzados | Spring Boot, JPA, Hibernate | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/06-UD2-Anexo-JavaGenéricosEstrDat.md | JavaGenéricosEstrDat.md | Apunte/Anexo | Transversal | Genéricos y estructuras de datos Java | Java | Vigente | Podría ir a 00-recursos-comunes |
| U2a_ApiRest_SpBoot/Documentos/07-FuncionamientoSpringBoot.md | FuncionamientoSpringBoot.md | Apunte | U2a | Funcionamiento interno Spring Boot | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/Documentos/UD2_Cap3-Sesion1.md | UD2_Cap3-Sesion1.md | Plan de sesión | U2a | Sesión 1 de U2 | - | Vigente | |
| U2a_ApiRest_SpBoot/demo/ | demo/ | Proyecto base | U2a | Proyecto demo Spring Boot | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/GestionEventos/ | GestionEventos/ | Proyecto base | U2a | Proyecto Gestión de Eventos | Spring Boot | Vigente | También existe el zip original |
| U2a_ApiRest_SpBoot/mini-spring-boot-tasks-completo/ | mini-spring-boot-tasks-completo/ | Proyecto base | U2a | Proyecto tareas Spring Boot completo | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/mini-spring-boot-tasks-git-lesson/ | mini-spring-boot-tasks-git-lesson/ | Proyecto base | U2a | Proyecto tareas + lección git | Spring Boot, Git | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/ArranqueProyecto.md | ArranqueProyecto.md | Guía | U2a (Battleship) | Guía de arranque del proyecto | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/AutorLibro/ | AutorLibro/ | Proyecto base | U2a | Ejemplo AutorLibro relacionado | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/battleship2/ | battleship2/ | Proyecto base | U2a | Versión alternativa Battleship | Spring Boot | Vigente | Posible duplicado |
| U2a_ApiRest_SpBoot/ProyectoGuia/ComoTestInsomnia.md | ComoTestInsomnia.md | Guía | U2a | Testing con Insomnia | Insomnia | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/ComoTestProyecto.md | ComoTestProyecto.md | Guía | U2a | Testing del proyecto | - | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/diagrama.mmd | diagrama.mmd | Diagrama | U2a | Diagrama arquitectura (Mermaid) | Mermaid | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/diagrama.puml | diagrama.puml | Diagrama | U2a | Diagrama arquitectura (PlantUML) | PlantUML | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/DiagramaDeDomino.plant | DiagramaDeDomino.plant | Diagrama | U2a | Diagrama de dominio | PlantUML | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/dwes-battleship/ | dwes-battleship/ | Proyecto base | U2a | Proyecto Battleship completo | Spring Boot | Vigente | Versión principal |
| U2a_ApiRest_SpBoot/ProyectoGuia/DWES_HundirLaFlota_BASIC.postman_collection.json | Postman básico | Colección tests | U2a | Tests Postman básicos | Postman | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/DWES_HundirLaFlota_TESTS.postman_collection.json | Postman tests | Colección tests | U2a | Tests Postman avanzados | Postman | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/main/ | main/ | Código | U2a | Código principal del proyecto | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/ProyectoGuia-Curso.md | ProyectoGuia-Curso.md | Guía | U2a | Planificación temporal del proyecto | - | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/uso_zips_battleship.md | uso_zips_battleship.md | Guía | U2a | Instrucciones de uso de los zips | - | Vigente | |
| U2a_ApiRest_SpBoot/ProyectoGuia/dwes-battleship-*.zip | 15 zips varios | Proyecto base (ZIP) | U2a | Versiones del proyecto Battleship (e1..e6, auth-skeleton, backend-final, classroom-template, frontend-skeleton, unified v1-v3, unified final, final-cors, final-with-compose) | Spring Boot | Archivo/versiones | Requiere depuración: decidir cuáles son las versiones canónicas |
| U2a_ApiRest_SpBoot/Seguridad/Ejemplo1/ | Ejemplo1/ | Proyecto base | U2a | Ejemplo seguridad Spring Boot | Spring Security | Vigente | |
| U2a_ApiRest_SpBoot/Seguridad/GestBibliotecaPaP-API/ | GestBibliotecaPaP-API/ | Proyecto base | U2a | Seguridad: gestión biblioteca | Spring Security | Vigente | |
| U2a_ApiRest_SpBoot/SpngBoot-4/demoSpBt4Kt/ | demoSpBt4Kt/ | Proyecto base | U2a | Spring Boot 4 + Kotlin + GraalVM | Spring Boot 4, Kotlin, GraalVM | Nuevo | Evaluar si entra en el curso |
| U2a_ApiRest_SpBoot/SpngBoot-4/PractSpngBoot4Java-GraalVM.md | PractSpngBoot4Java-GraalVM.md | Práctica | U2a | Práctica Spring Boot 4 + GraalVM (Java) | Spring Boot 4, GraalVM | Nuevo | |
| U2a_ApiRest_SpBoot/SpngBoot-4/PractSpngBoot4Kotlin-GraalVM.md | PractSpngBoot4Kotlin-GraalVM.md | Práctica | U2a | Práctica Spring Boot 4 + GraalVM (Kotlin) | Spring Boot 4, GraalVM | Nuevo | |
| U2a_ApiRest_SpBoot/tarea/01-SeguridadJWT.md | 01-SeguridadJWT.md | Enunciado tarea | U2a | Tarea seguridad JWT | Spring Security, JWT | Vigente | |
| U2a_ApiRest_SpBoot/tarea/book-catalog-template/ | book-catalog-template/ | Plantilla proyecto | U2a | Plantilla catálogo de libros | Spring Boot | Vigente | |
| U2a_ApiRest_SpBoot/tarea/book-catalog-tests-seed/ | book-catalog-tests-seed/ | Plantilla tests | U2a | Tests semilla para catálogo libros | Spring Boot, JUnit | Vigente | |
| U2a_ApiRest_SpBoot/tarea/tareaMVC/ | tareaMVC/ | Proyecto base | U2a | Tarea MVC | Spring Boot, MVC | Vigente | |
| U2a_ApiRest_SpBoot/versiones/ | versiones/ (11 zips) | Archivo histórico | U2a | Versiones históricas mini-spring-boot-tasks | Spring Boot | Archivo | Para 90-archivo/ |

### U2b — API REST con .NET

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U2b_DotNetApiREST/MiApi/ | MiApi/ | Proyecto base | U2b | Scaffold API .NET | .NET/C# | Vigente | |
| U2b_DotNetApiREST/RecetasApi/ | RecetasApi/ | Proyecto base | U2b | API de recetas .NET | .NET/C# | Vigente | |
| U2b_DotNetApiREST/Tareas/ | Tareas/ | Proyecto base | U2b | Tareas .NET | .NET/C# | Vigente | |
| U2b_DotNetApiREST/ToDo_Api/ | ToDo_Api/ | Proyecto base | U2b | API ToDo .NET | .NET/C# | Vigente | También existe el zip |

### U2c — GraphQL

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U2c-GraphQL-HotChocolate-dotNet/00-GraphQL-DotNet-HotChocolate.md | 00-GraphQL-DotNet-HotChocolate.md | Apunte | U2c | GraphQL con HotChocolate en .NET | GraphQL, HotChocolate, .NET | Sustituido | Reemplazado por una comparación conceptual mínima entre GraphQL, REST y HTTP QUERY |

### U3 — Spring Boot MVC

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U3_SpBootMVC/SpringMV-Tareas_IdeasAtrabajar.md | SpringMV-Tareas_IdeasAtrabajar.md | Apunte/Ideas | U3 | Ideas de tareas Spring MVC | Spring MVC | Vigente | |
| U3_SpBootMVC/SpringMVC_Mustache.md | SpringMVC_Mustache.md | Apunte | U3 | Spring MVC + Mustache | Spring MVC, Mustache | Vigente | |
| U3_SpBootMVC/SpringMVC_Thymeleaf.md | SpringMVC_Thymeleaf.md | Apunte | U3 | Spring MVC + Thymeleaf | Spring MVC, Thymeleaf | Vigente | |
| U3_SpBootMVC/SpringMVC-GestorTareas.md | SpringMVC-GestorTareas.md | Apunte | U3 | Gestor de tareas | Spring MVC | Vigente | |
| U3_SpBootMVC/SpringMVC.md | SpringMVC.md | Apunte | U3 | Apunte general Spring MVC | Spring MVC | Vigente | |
| U3_SpBootMVC/SpringMVC/ | SpringMVC/ | Proyecto base | U3 | Código de ejemplo | Spring MVC | Vigente | |
| U3_SpBootMVC/Tareas/ | Tareas/ | Tareas | U3 | Tareas Spring MVC | Spring MVC | Vigente | |
| U3_SpBootMVC/TareaSpringMVC-Productos-Profe.md | TareaSpringMVC-Productos-Profe.md | Enunciado (profe) | U3 | Tarea productos (versión profesor) | Spring MVC | Vigente | Contiene soluciones |
| U3_SpBootMVC/TareaSpringMVC-Productos.md | TareaSpringMVC-Productos.md | Enunciado tarea | U3 | Tarea productos (versión alumno) | Spring MVC | Vigente | |

### U4 — PHP

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U4_PHP/00-PHP_documentor-Guia.md | 00-PHP_documentor-Guia.md | Guía | U4 | Guía de phpDocumentor | PHP, phpDocumentor | Vigente | |
| U4_PHP/01-PHP_Basics/ | 01-PHP_Basics/ | Apuntes + ejercicios | U4 | PHP básico: sintaxis, condicionales, bucles, arrays, funciones. Fuentes org/tex + PDF + código + docker-compose | PHP, Docker | Vigente | Formato org-mode. Muy completo |
| U4_PHP/02-PHP_Advanced/ | 02-PHP_Advanced/ | Apuntes + ejercicios | U4 | PHP avanzado: paso datos HTML, formularios, sesiones/cookies, BBDD | PHP | Vigente | |
| U4_PHP/03-PHP_CRUD/ | 03-PHP_CRUD/ | Apuntes + ejercicios | U4 | CRUD PHP concesionario coches | PHP, BBDD | Vigente | |
| U4_PHP/04-PHP_OOP/ | 04-PHP_OOP/ | Apuntes + ejercicios | U4 | PHP orientado a objetos | PHP, OOP | Vigente | |
| U4_PHP/05-PHP_OOP-CRUD/ | 05-PHP_OOP-CRUD/ | Apuntes + ejercicios | U4 | PHP OOP CRUD (ToDo) | PHP, OOP, CRUD | Vigente | |
| U4_PHP/ActividadesCls/CRUD concesionario-20221021/ | CRUD concesionario/ | Proyecto base | U4 | CRUD concesionario PHP | PHP | Vigente | |
| U4_PHP/ActividadesCls/GestionTareas/ | GestionTareas/ | Proyecto base | U4 | Gestión de tareas PHP | PHP | Vigente | |
| U4_PHP/ActividadesCls/Quizz/ | Quizz/ | Proyecto base | U4 | Quizz PHP | PHP | Vigente | |
| U4_PHP/PHP_Necesario_Laravel12/PHP_Necesario_Laravel-12.md | PHP_Necesario_Laravel-12.md | Apunte | U4→U5 | PHP necesario para Laravel 12 (fuente) | PHP, Laravel | Vigente | Puente entre unidades |
| U4_PHP/PHP_Necesario_Laravel12/PHP_Necesario_Laravel-12.html | PHP_Necesario_Laravel-12.html | Apunte (publicado) | U4→U5 | Versión HTML | PHP, Laravel | Vigente | Derivado |
| U4_PHP/TareaPHP/formulario_php_quizz.gift | formulario_php_quizz.gift | Banco preguntas | U4 | Preguntas GIFT para Moodle | Moodle, GIFT | Vigente | |
| U4_PHP/TareaPHP/TareaRapidaPHP-2025_2026/ | TareaRapidaPHP/ | Tarea | U4 | Tarea rápida PHP curso 2025/2026 | PHP | Vigente | |

### U5 — Laravel

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U5_Laravel/Documentacion/Laravel-HTTP2_Bson.md | Laravel-HTTP2_Bson.md | Apunte | U5 | Laravel HTTP/2 y BSON | Laravel | Pendiente revisión | Contenido muy específico |
| U5_Laravel/LaravelApi/LaravelAPI-Only.md | LaravelAPI-Only.md | Apunte | U5 | API Laravel sin frontend | Laravel | Vigente | |
| U5_Laravel/Proyecto/ | Proyecto/ | Proyecto base | U5 | Proyecto Laravel completo con src/, Documentacion, .git | Laravel | Vigente | Contiene script repo_a_texto.py en Correccion/ |
| U5_Laravel/sail/Blog-Laravel-10/ | Blog-Laravel-10/ | Proyecto base | U5 | Blog Laravel 10 + Sail | Laravel 10, Sail, Docker | Vigente | Versión L10 |
| U5_Laravel/sail/docker-compose.yml | docker-compose.yml | Configuración | U5 | Docker Compose Sail | Docker, Sail | Vigente | |
| U5_Laravel/sail/Laravel10-api/ | Laravel10-api/ | Proyecto base | U5 | API Laravel 10 + Sail | Laravel 10, Sail | Vigente | |
| U5_Laravel/sail/Laravel11-api/ | Laravel11-api/ | Proyecto base | U5 | API Laravel 11 + Sail | Laravel 11, Sail | Vigente | |
| U5_Laravel/sail/Laravel12-api/ | Laravel12-api/ | Proyecto base | U5 | API Laravel 12 + Sail | Laravel 12, Sail | Vigente (novedad) | Última versión |
| U5_Laravel/sail/recetas-api-laravel12/ | recetas-api-laravel12/ | Proyecto base | U5 | API recetas Laravel 12 + Sail | Laravel 12, Sail | Vigente (novedad) | |
| U5_Laravel/laravel-24_25/ | laravel-24_25/ | Backup/Archivo | U5 | Copia completa del curso anterior (Documentacion, LaravelApi, Proyecto, sail) | Laravel | Archivo | Duplica material. Revisar si hay contenido no duplicado |

### U6 — Aplicaciones Híbridas

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U6/UD6-AplicacionesHibridas.md | UD6-AplicacionesHibridas.md | Apunte | U6 | Concepto de aplicaciones híbridas | - | Vigente | Unidad muy pequeña |

### Java / Previos (00-unidad-0-previos)

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| Java/guion_de_clase_java_hasta_25_para_estudiantes_de_kotlin.md | Guión Java 17-25 | Guión de clase | Previos | 6+ sesiones Java moderno para estudiantes con base Kotlin | Java 17-25, Gradle, JUnit | Vigente | Material excelente para 00-unidad-0-previos |
| Java/GeonotesTarea.md | GeonotesTarea.md | Enunciado tarea | Previos | Tarea GeoNotes (proyecto Java) | Java 21 | Vigente | |
| Java/calc-api.zip | calc-api.zip | Proyecto base | Previos | API calculadora Java | Java | Vigente | |
| Java/calc21/ | calc21/ | Proyecto base | Previos | Calculadora Java 21 | Java 21 | Vigente | |
| Java/geonotes-*.zip | geonotes-*.zip (8 variantes) | Proyecto base (ZIP) | Previos | Múltiples versiones GeoNotes | Java 21, Gradle | Archivo/versiones | Consolidar a 1-2 versiones |
| Java/java_vs_kotlin_excepciones.pdf | java_vs_kotlin_excepciones.pdf | Apunte | Previos | Comparativa excepciones Java vs Kotlin | Java, Kotlin | Vigente | |
| Java/java_vs_kotlin_excepciones_extendido.pdf | java_vs_kotlin_excepciones_extendido.pdf | Apunte | Previos | Versión extendida | Java, Kotlin | Vigente | |
| Java/java_vs_kotlin_excepciones_presentacion.pdf | java_vs_kotlin_excepciones_presentacion.pdf | Presentación | Previos | Presentación excepciones | Java, Kotlin | Vigente | |

### Exámenes y Evaluación Global

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| Examenes/00-Examen-Servidor.md | 00-Examen-Servidor.md | Examen | Evaluación | Examen de servidor | - | Vigente | |
| Examenes/01-CheckList-Rubrica.md | 01-CheckList-Rubrica.md | Rúbrica | Evaluación | Checklist/rúbrica de evaluación | - | Vigente | |
| Examenes/01-CheckList-Rubrica.html | 01-CheckList-Rubrica.html | Rúbrica (HTML) | Evaluación | Versión HTML | - | Vigente | Derivado |

### Transversales / Varios

| Ruta | Elemento | Tipo | Bloque/Unidad | Descripción | Herramientas | Estado aparente | Observaciones |
| ---- | -------- | ---- | ------------- | ----------- | ------------ | --------------- | ------------- |
| U2a_ApiRest_SpBoot/Documentos/00-Utils_ChuletaGIT.md | ChuletaGIT.md | Guía | Transversal | Chuleta comandos GIT | Git | Vigente | Reubicar a 00-recursos-comunes |
| U2a_ApiRest_SpBoot/Documentos/06-UD2-Anexo-JavaGenéricosEstrDat.md | JavaGenéricosEstrDat.md | Apunte | Transversal | Genéricos Java | Java | Vigente | Reubicar a 00-recursos-comunes |
| U2a_ApiRest_SpBoot/Documentacion/Laravel/ | Laravel/ | Documentación | ¿U5? | Docs Laravel dentro de U2a | Laravel | Fuera de sitio | Revisar y reubicar |
| DWES/Unidades/UD2/ | UD2/ | ¿Material antiguo? | ¿Curso anterior? | Estructura anidada huérfana | - | Pendiente revisión | Revisar si contiene material no duplicado |

### Prompts docentes reutilizables

| Ruta | Elemento | Tipo | Descripción |
| ---- | -------- | ---- | ----------- |
| prompt_inventario_material_modulo.md | prompt_inventario_material_modulo.md | Prompt plantilla | Prompt genérico de inventario (placeholders) |
| U5_Laravel/Proyecto/Correccion/repo_a_texto.py | repo_a_texto.py | Script corrección | Convierte repos a texto para corrección con IA |

## 3. Materiales de corrección detectados

Solo se incluyen elementos reutilizables (prompts, guías, instrucciones, herramientas):

| Ruta | Elemento | Uso probable | Unidad/Bloque | Observaciones |
| ---- | -------- | ------------ | ------------- | ------------- |
| U5_Laravel/Proyecto/Correccion/repo_a_texto.py | repo_a_texto.py | Script para convertir repositorios a texto plano y facilitar corrección con IA | U5 | Reutilizable como herramienta de profesor |
| U2a_ApiRest_SpBoot/ProyectoGuia/ComoTestInsomnia.md | ComoTestInsomnia.md | Guía de testing para el alumnado (no es de corrección, pero sirve para autoevaluación) | U2a | |
| U2a_ApiRest_SpBoot/ProyectoGuia/ComoTestProyecto.md | ComoTestProyecto.md | Instrucciones generales de testing | U2a | |

## 4. Elementos excluidos deliberadamente

| Categoría | Rutas o patrones detectados | Motivo |
| --------- | -------------------------- | ------ |
| ZIPs de entregas de alumnado | ProyectoConjunto/2081-Entrega_1_ModeloDeDatos-547503.zip, ProyectoConjunto/2574-Prueba Práctica Proyecto Intermodular 2026-604721.zip | Contienen entregas de alumnos |
| Repositorios de alumnado | U5_Laravel/ProyectoCorreccion/AcedoJavier/, U5_Laravel/ProyectoCorreccion/VillateAitana/, U4_PHP/TareaPHP/2dawa_24-25-quizz-app-dwes-php/ | Código de alumnos no reutilizable como material docente |
| Descompresiones de entregas | ProyectoConjunto/2081-Entrega_1_ModeloDeDatos-547503/ | Extraído de ZIP de alumno |
| Artefactos de corrección | U2a_ApiRest_SpBoot/tarea/correccionCatalogoLibros/, U2a_ApiRest_SpBoot/tarea/correccionGestionEventos/, U2a_ApiRest_SpBoot/tarea/correccionKataApiCerveza/, U4_PHP/TareaPHP/Correccion/, Java/correcciones/ | Carpetas de corrección con feedback/notas. Revisar si contienen prompts reutilizables |
| Posible material de alumno | U2a_ApiRest_SpBoot/Recupera_y_Mejora/ (contiene SanzAznarPablo.md~) | Hace referencia a alumno individual |

## 5. Riesgos, duplicidades y material dudoso

### Material duplicado o con múltiples versiones

| Elemento | Problema | Propuesta |
| -------- | -------- | --------- |
| Battleship (U2a) — 15 zips en ProyectoGuia/ + 11 zips en versiones/ + battleship2/ + dwes-battleship/ + código main/ + AutorLibro/ | Saturación de versiones, difícil saber cuál es la canónica | Conservar 2-3 versiones: skeleton base, template aula, final unificado. El resto a 90-archivo/ |
| GeoNotes (Java) — 8 zips | Demasiadas variantes (con tests, con ejemplos, packaged, teaching...) | Conservar 1-2: la versión teaching y la versión con tests |
| Laravel (U5) — laravel-24_25/ duplica toda la estructura | Backup del curso anterior dentro de la unidad activa | Revisar si hay contenido único; si no, archivar o eliminar |
| Documentación Laravel dentro de U2a | Docs de Laravel mezcladas con Spring Boot | Reubicar a U5 o a 00-recursos-comunes |
| DWES/Unidades/UD2/ | Estructura anidada extraña, posible vestigio de organización anterior | Revisar contenido y decidir si integrar o archivar |

### Material obsoleto o con tecnologías antiguas

| Elemento | Riesgo | Propuesta |
| -------- | ------ | --------- |
| Rmd (R Markdown) como formato de apuntes | R es estadística, no es el estándar para documentación técnica. ¿Sigue siendo el formato activo? | Verificar si el docente sigue usando Rmd o migró a md/org |
| Laravel 10 (Blog-Laravel-10, Laravel10-api) | Laravel está en 12, L10 puede quedar obsoleto en 2026 | Evaluar si se mantienen como ejemplos históricos o se actualizan |
| Spring Boot 3 (mayoría del material) vs Spring Boot 4 | SpngBoot-4/ indica que ya hay material para la versión 4 | Decidir si el curso migra a SB4 o se queda en SB3 con mención a SB4 |
| PHP org-mode + LaTeX | Formato org-mode no es mainstream, requiere emacs | Verificar si el docente puede/mantiene ese flujo de trabajo |

### Material con contexto poco claro

| Elemento | Problema |
| -------- | -------- |
| FicheroDeTexto.txt (ProyectoGuia/) | Sin contexto |
| nueva-copia/ (tarea/) | Sin contexto |
| repo-materiales-update.zip (ProyectoGuia/) | Sin descripción |
| changelog-tools.zip (ProyectoGuia/) | Utilidad sin contexto |

### Unidades infradotadas

| Unidad | Problema |
| ------ | -------- |
| U2c (GraphQL) | Solo 1 documento. Si se mantiene como unidad separada, necesita más material |
| U6 (Aplicaciones Híbridas) | Solo 1 documento conceptual. Evaluar si se fusiona con otra unidad o se expande |

## 6. Recomendaciones para la siguiente fase

### Estructura común propuesta para todas las unidades

```
udXX-nombre-unidad/
├── README.md
├── 01-teoria/        → Apuntes, documentos fuente
├── 02-ejemplos/      → Ejemplos no evaluables, demos
├── 03-practicas/     → Guiones de laboratorio, prácticas
├── 04-evaluacion/    → Enunciados evaluables, rúbricas
├── 05-recursos/      → Datasets, plantillas, packs
├── 90-archivo/       → Versiones antiguas, histórico
└── 99-profesor/      → Soluciones, notas, prompts corrección
```

### Decisiones a tomar antes de reorganizar

- **Unificación de U2a/b/c**: ¿Una sola unidad "API REST" con subvariantes tecnológicas, o unidades separadas por tecnología?
- **Ubicación de Java**: Ya tienes `00-unidad-0-previos/` con modelado MongoDB. Java encaja ahí como segundo previo (o como preparación específica para Spring Boot).
- **ProyectoConjunto**: ¿Va como unidad final separada o se integra en las unidades que lo alimentan?
- **Examenes**: ¿A `00-planificacion/` como evaluación global, o distribuido por unidades?
- **Spring Boot 4 + GraalVM**: ¿Entra en el curso 2026/2027 o se deja para más adelante?
- **Formato de apuntes**: Decidir si se mantiene Rmd/org/tex o se migra a Markdown como formato único.

### Separación de materiales

| Tipo | Destino |
| ---- | ------- |
| Material docente (apuntes, prácticas, proyectos base) | Unidades correspondientes |
| Material de corrección (prompts, guías) | `99-profesor/` por unidad |
| Entregas de alumnado (ZIPs, repos) | Excluir / Archivo histórico |
| Artefactos de evaluación (notas, feedback) | Excluir / Archivo docente |
| Recursos transversales (chuletas, anexos) | `00-recursos-comunes/` |

### Orden sugerido para la reorganización

1. Decidir estructura de unidades (resolver dudas de unificación arriba).
2. Separar/eliminar entregas de alumnado y artefactos de corrección.
3. Mover recursos transversales a `00-recursos-comunes/`.
4. Depurar versionado (Battleship, GeoNotes, Laravel backups).
5. Reorganizar cada unidad según la estructura propuesta.
6. Crear README por unidad y documento de estado.
