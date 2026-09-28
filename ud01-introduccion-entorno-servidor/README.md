# UD1 - Introducción al desarrollo web en servidor

## Propósito

Comprender qué ocurre entre el navegador y el servidor, cómo se ejecuta código en el backend y qué criterios permiten elegir lenguajes, runtimes, herramientas y frameworks. La unidad prepara el entorno Java del módulo y termina con un servidor mínimo verificable; el diseño de APIs REST completas comienza en UD2.

## Baseline

- Java 25 LTS.
- Spring Boot 4.
- Maven Wrapper incluido en cada proyecto.
- Git y un IDE con soporte para Java 25.
- HTTPie o cURL para inspección HTTP.

Las versiones concretas y los comandos de diagnóstico se fijarán en la guía de entorno. No se usará una versión indeterminada como "la última estable".

## Resultados de aprendizaje

Esta unidad trabaja RA1: seleccionar arquitecturas y tecnologías de programación web en servidor analizando sus capacidades y características.

| CE | Evidencia prevista |
|---|---|
| a | Comparar ejecución cliente y servidor mediante una traza explicada. |
| b | Justificar ventajas de generar contenido dinámico en el servidor. |
| c | Diferenciar CGI/FastCGI, procesos persistentes y contenedores de aplicaciones. |
| d | Comparar servidor web, proxy inverso, contenedor servlet, servidor embebido y servidor de aplicaciones. |
| e | Caracterizar lenguajes, runtimes y tecnologías de backend con criterios técnicos. |
| f | Demostrar HTML generado en servidor y una respuesta JSON mínima. |
| g | Comparar herramientas y frameworks y justificar la selección usada en el módulo. |

## Recorrido

1. [Cliente, servidor y contenido dinámico](01-documentacion/01-cliente-servidor-y-contenido-dinamico.md).
2. [Mecanismos de ejecución en servidor](01-documentacion/02-mecanismos-ejecucion-servidor.md).
   [Demo Nginx + fcgiwrap + Python CGI](02-ejemplos/cgi-fastcgi-python/README.md) y
   [tarea breve de observación y cambio](03-ejercicios/04-explorar-cgi-fastcgi/README.md).
3. [Servidores web, proxies y contenedores](01-documentacion/03-infraestructura-web.md).
4. [Tecnologías, frameworks e integración](01-documentacion/04-tecnologias-e-integracion.md).
5. [Versiones y estructura de los mensajes HTTP](01-documentacion/05-versiones-y-mensajes-http.md).
6. [Métodos HTTP: GET, POST, PUT, PATCH, DELETE, HEAD y OPTIONS](01-documentacion/06-metodos-http.md).
7. [Estados de respuesta HTTP: 2xx, 3xx, 4xx y 5xx](01-documentacion/07-estados-http.md).
8. [Cabeceras HTTP, contenido, caché y negociación](01-documentacion/08-cabeceras-http-contenido-y-cache.md).
9. [Método HTTP QUERY y consultas con contenido](01-documentacion/09-query-http.md).
10. [HTTP seguro: TLS, cookies, cabeceras y trazas](06-seguridad/README.md).
11. [Entorno reproducible con Java 25 y Spring Boot 4](01-documentacion/10-entorno-java25-spring-boot4.md).
12. [Hello Server con HTML, JSON, `/health` y pruebas](02-ejemplos/hello-server/README.md).
13. [Laboratorio de análisis HTTP](03-ejercicios/01-analisis-http/README.md).

La documentación de cada apartado está enlazada en el recorrido anterior.

## Evaluación

La [evaluación de UD1](EVALUACION.md) en 2026/2027 combina:

- [práctica cooperativa HTTP y Hello Server](03-ejercicios/03-practica-cooperativa-2026/README.md): 65 %;
- cuestionarios Moodle individuales sobre RA1.a-g: 35 %.

## Seguridad

La primera unidad introduce hábitos que se mantienen durante todo el módulo:

- No entregar cookies, tokens, credenciales ni cabeceras `Authorization` reales.
- Validar certificados y usar HTTPS en servicios externos.
- Tratar cabeceras y banners tecnológicos como información potencialmente sensible.
- Analizar `Secure`, `HttpOnly`, `SameSite`, HSTS, CSP y protección frente a inclusión en marcos.

## Frontera con UD2

UD1 usa endpoints mínimos para observar HTTP e integración HTML/JSON. CRUD, diseño REST, validación de entrada, OpenAPI, autenticación, autorización y Battleship pertenecen a UD2.
