# Rúbrica Común del Módulo por RA/CE — DWES 2026/2027

Esta rúbrica transversal sirve como marco de evaluación para todas las entregas prácticas, proyectos y exámenes del módulo de **Desarrollo Web en Entorno Servidor (DWES)**. Está alineada con los 9 Resultados de Aprendizaje (RA) y Criterios de Evaluación (CE) oficiales, ponderados según su madurez técnica, seguridad y calidad de código.

---

## Niveles Generales de Logro (Escala Transversal)

Para cada criterio evaluado, se aplican los siguientes niveles de rendimiento:

| Nivel | Nota | Criterios de Calidad de Código, TDD y Seguridad |
| :--- | :--- | :--- |
| **Insuficiente (IN)** | `0 - 4.9` | El código no compila o presenta errores graves de ejecución. No hay separación de capas (código espagueti). No se incluyen pruebas automatizadas o todas fallan. Existen vulnerabilidades críticas de seguridad (inyección SQL, XSS, contraseñas en texto plano). |
| **Suficiente (SU)** | `5.0 - 5.9` | La funcionalidad mínima requerida funciona bajo caminos felices (happy paths). Hay un intento de estructuración, pero se arrastra deuda técnica. Las pruebas son inexistentes o puramente testimoniales. La seguridad se limita a configuraciones por defecto. |
| **Notable (NT)** | `6.0 - 8.9` | Implementación sólida y robusta. Separación clara de responsabilidades (controlador, servicio, repositorio/modelo). Se manejan correctamente los errores y excepciones de negocio. Se incluyen pruebas funcionales y unitarias automáticas con aserciones correctas. Validación de datos de entrada y sanitización activa. |
| **Sobresaliente (SB)** | `9.0 - 10` | Código limpio de nivel profesional. Uso correcto de patrones de diseño (ActiveRecord en Laravel, DataMapper/JPA en Spring Boot). Pruebas automatizadas exhaustivas (caminos felices y casos de error). Seguridad robusta (JWT/Sanctum explícito, CORS configurado, políticas de autorización de recursos). Optimización de rendimiento (paginación eficiente con Slice/Page, transacciones de solo lectura). |

---

## Detalle de Evaluación por Resultado de Aprendizaje (RA)

### RA 1: Arquitecturas y tecnologías de servidor
*Selecciona las arquitecturas y tecnologías de programación web en entorno servidor, analizando sus capacidades y características.*
* **Evidencias clave**: Justificación de la arquitectura elegida (Spring Boot, Laravel), diagramas de despliegue, configuración de contenedores Docker/Sail.

* **CE a, b, c, d, e, f, g**:
  * **SU (5.0)**: Identifica y utiliza el servidor embebido (Tomcat) o contenedor estándar sin modificar configuraciones. Levanta el entorno básico mediante plantillas dadas.
  * **NT (8.0)**: Configura contenedores Docker independientes para bases de datos (PostgreSQL/H2) y servicios necesarios, integrándolos de forma programática.
  * **SB (10)**: Justifica de forma arquitectónica la separación cliente-servidor (APIs REST inmutables). Configura y tunear el servidor embebido (hilos de Tomcat, timeouts) o el entorno de ejecución de contenedores (WSL2, Podman) optimizando rendimiento y puertos.

---

### RA 2: Sentencias ejecutables en servidor
*Escribe sentencias ejecutables por un servidor web reconociendo y aplicando procedimientos de integración del código en lenguajes de marcas.*
* **Evidencias clave**: Sintaxis correcta del lenguaje (Java 25 / PHP 8.x), uso de motores de plantillas (Thymeleaf / Blade) o controladores de respuesta REST.

* **CE a, b, c, d, e, f, g, h**:
  * **SU (5.0)**: Escribe sentencias básicas y variables respetando la sintaxis del lenguaje. El código mezcla lógica de negocio en la presentación de forma puntual.
  * **NT (8.0)**: Usa correctamente tipos de datos avanzados, variables inmutables (Java Records, tipos nativos de PHP) y scopes adecuados para evitar fugas de memoria.
  * **SB (10)**: Estructuración impecable del tipado. Compilación con el flag `-parameters` activo para que el framework resuelva correctamente las anotaciones en tiempo de ejecución. Ausencia total de código duplicado u obsoleto.

---

### RA 3: Estructuras de programación embebidas
*Escribe bloques de sentencias embebidos en lenguajes de marcas, seleccionando y utilizando las estructuras de programación.*
* **Evidencias clave**: Estructuras de control, manipulación de matrices/colecciones, validación de formularios clásicos y comentarios de código.

* **CE a, b, c, d, e, f, g**:
  * **SU (5.0)**: Control de flujo básico (condicionales y bucles). Formularios básicos con mapeo simple de parámetros de petición. Comentarios escasos o redundantes.
  * **NT (8.0)**: Funciones y métodos bien modularizados. Uso intensivo de colecciones seguras (Streams en Java, Colecciones de Laravel). Validación básica server-side de formularios.
  * **SB (10)**: Lógica estructurada en bloques limpios y reutilizables. Tratamiento avanzado de arrays y colecciones con operaciones funcionales complejas. Comentarios limpios que explican el *porqué* del diseño y no el *cómo*.

---

### RA 4: Desarrollo de aplicaciones con gestión de estado y autenticación
*Desarrolla aplicaciones web embebidas analizando e incorporando funcionalidades de mantenimiento de estado y autenticación.*
* **Evidencias clave**: Cookies, Sesiones HTTP, autenticación JWT (Spring Security 7) y tokens persistentes (Laravel Sanctum).

* **CE a, b, c, d, e, f**:
  * **SU (5.0)**: Autenticación básica por defecto. Manejo básico de variables de sesión tradicionales.
  * **NT (8.0)**: Autenticación basada en Base de Datos conectando `UserDetailsService` (Spring) o Providers (Laravel). Gestión segura de contraseñas mediante encriptación fuerte (BCrypt). Sesiones sin estado (stateless) para APIs REST.
  * **SB (10)**: Implementación dual y aislada de seguridad (Thymeleaf/MVC protegido con sesión + CSRF obligatorio; API REST protegida por JWT/Sanctum sin estado). Configuración programática explícita de filtros de seguridad en el DSL de Spring Security 7 sin encadenamientos obsoletos (`.and()`).

---

### RA 5: Separación de la lógica de presentación de la de negocio
*Desarrolla aplicaciones aplicando mecanismos para separar la presentación de la lógica de negocio (MVC, Clean Architecture).*
* **Evidencias clave**: Controladores delgados, Capa de Servicios independientes, DTOs inmutables, pruebas automatizadas.

* **CE a, b, c, d, e, f, g, h**:
  * **SU (5.0)**: Implementa la funcionalidad en controladores gigantescos (controladores que consultan bases de datos directamente). Código acoplado.
  * **NT (8.0)**: Arquitectura de 3 capas limpia (Controlador -> Servicio -> Repositorio). Uso de DTOs para no exponer entidades de base de datos. Pruebas funcionales escritas tras codificar.
  * **SB (10)**: Flujo de trabajo **TDD-First** (test rojo -> implementación -> test verde). Controladores extremadamente delgados que solo manejan HTTP. Lógica de negocio encapsulada al 100% en Servicios. Uso de Java 25 Records o clases inmutables para DTOs. Cobertura de tests robusta para casos límite.

---

### RA 6: Acceso a almacenes de datos y seguridad de la información
*Desarrolla aplicaciones web de acceso a almacenes de datos, aplicando medidas para mantener la seguridad y la integridad.*
* **Evidencias clave**: Conectividad JPA/Hibernate o Eloquent, migraciones de esquema (Flyway/Artisan), transaccionalidad, prevención de inyección SQL.

* **CE a, b, c, d, e, f, g**:
  * **SU (5.0)**: Creación de tablas de forma automática (hbm2ddl=update). Consultas JPA básicas o ActiveRecord directo sin control de transacciones.
  * **NT (8.0)**: Control estricto del esquema mediante migraciones controladas (Flyway o Laravel migrations). Consultas optimizadas sin problemas de N+1 (uso de Eager/Lazy estructurado). Gestión explícita de transacciones en la capa de servicios (`@Transactional`).
  * **SB (10)**: Migraciones versionadas en entornos productivos (PostgreSQL). Estrategias de IDs seguras (UUIDs optimizados). Control de seguridad de la base de datos (evitar inyección SQL mediante consultas parametrizadas o Query Builders seguros). Optimización de rendimiento usando transacciones de solo lectura y proyecciones directas a DTOs.

---

### RA 7: Desarrollo de servicios web (APIs REST)
*Desarrolla servicios web reutilizables y accesibles mediante protocolos web, verificando y documentando su funcionamiento.*
* **Evidencias clave**: Endpoints RESTful coherentes, formateo JSON con API Resources, documentación OpenAPI/Swagger UI, pruebas con HTTPie.

* **CE a, b, c, d, e, f, g, h**:
  * **SU (5.0)**: Endpoints REST inconsistentes (verbos mal aplicados, ej. GET para borrar). Devuelve entidades directas de base de datos.
  * **NT (8.0)**: API REST consistente con las guías de diseño. Uso obligatorio de API Resources para la transformación consistente de JSON. Documentación básica autogenerada de Swagger/SpringDoc. Pruebas de endpoints usando herramientas CLI (HTTPie).
  * **SB (10)**: API REST profesional. Gestión avanzada de errores: excepciones de dominio (`DomainException`) capturadas de forma global y transformadas en payloads JSON de error con códigos de negocio (`RECETA_PUBLICADA`, `CREDENCIALES_INVALIDAS`) y códigos HTTP semánticos (409 Conflict, 401 Unauthorized, etc.). Documentación Swagger UI completa con especificaciones de seguridad.

---

### RA 8: Generación dinámica uniendo servidor y cliente
*Genera páginas web dinámicas analizando y utilizando tecnologías del servidor que añadan código al lenguaje de marcas.*
* **Evidencias clave**: Interacciones Fetch/AJAX, validaciones dinámicas en cliente coordinadas con el backend, renderizado parcial.

* **CE a, b, c, d, e, f, g**:
  * **SU (5.0)**: Formularios que provocan recarga completa de la página para cualquier interacción.
  * **NT (8.0)**: Envío de datos y lectura mediante llamadas Fetch/AJAX asíncronas hacia la API REST. Manipulación básica del DOM resultante.
  * **SB (10)**: Interacción dinámica cliente-servidor fluida. Validaciones asíncronas en tiempo real (ej. comprobar disponibilidad de username) procesadas de forma eficiente por controladores ligeros en el servidor. Tratamiento elegante del flujo de UI tras recibir códigos de error REST en JSON.

---

### RA 9: Aplicaciones web híbridas y heterogéneas
*Desarrolla aplicaciones web híbridas seleccionando y utilizando tecnologías, frameworks servidor y repositorios heterogéneos.*
* **Evidencias clave**: Consumo de APIs de terceros, procesamiento de repositorios heterogéneos (JSON/CSV/XML), integración idempotente con trazabilidad de procedencia.

* **CE a, b, c, d, e, f, g, h**:
  * **SU (5.0)**: Lectura cruda de archivos JSON estáticos en el servidor.
  * **NT (8.0)**: Consumo programático de una API externa en tiempo de ejecución (WebClient o similar) procesando las respuestas y manejando fallos de red. Integración de al menos dos fuentes heterogéneas. Trazabilidad básica de procedencia.
  * **SB (10)**: Integración híbrida robusta con múltiples fuentes, persistencia idempotente, trazabilidad completa de procedencia y licencias. Análisis agregado reproducible que demuestra el valor de la integración. La IA (vía Spring AI o similar) es una vía opcional para el CE g, no un requisito. La misma calificación se alcanza con rigor en el análisis, transformación y verificación sin IA.
