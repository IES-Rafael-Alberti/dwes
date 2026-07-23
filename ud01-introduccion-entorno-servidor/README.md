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
3. [Servidores web, proxies y contenedores](01-documentacion/03-infraestructura-web.md).
4. [Tecnologías, frameworks e integración](01-documentacion/04-tecnologias-e-integracion.md).
5. Integración entre HTML y código servidor.
6. HTTP actual: versiones, métodos, estados, cabeceras, contenido y caché.
7. HTTP seguro: TLS, certificados, cookies, cabeceras y trazas.
8. Entorno reproducible con Java 25 y Spring Boot 4.
9. Hello Server con HTML, JSON, `/health` y pruebas.
10. [Laboratorio de análisis HTTP](03-ejercicios/01-analisis-http/README.md).

La documentación canónica se publica en `01-documentacion/` siguiendo este orden. Los Rmd, PDF y planes de sesión heredados permanecen fuera de la navegación mientras se consolida su contenido válido.

## Evaluación

- Laboratorio HTTP con trazas sanitizadas.
- Comparación razonada de tecnologías y mecanismos de ejecución.
- Demostración reproducible del entorno y del servidor mínimo.
- Cuestionario que cubra RA1.a-g.

La ponderación definitiva se fijará al reformar los instrumentos. Hasta entonces, ninguna actividad aislada representa por sí sola toda la evaluación de UD1.

## Seguridad

La primera unidad introduce hábitos que se mantienen durante todo el módulo:

- No entregar cookies, tokens, credenciales ni cabeceras `Authorization` reales.
- Validar certificados y usar HTTPS en servicios externos.
- Tratar cabeceras y banners tecnológicos como información potencialmente sensible.
- Analizar `Secure`, `HttpOnly`, `SameSite`, HSTS, CSP y protección frente a inclusión en marcos.

## Frontera con UD2

UD1 usa endpoints mínimos para observar HTTP e integración HTML/JSON. CRUD, diseño REST, validación de entrada, OpenAPI, autenticación, autorización y Battleship pertenecen a UD2.

## Estado de reforma

El diagnóstico, backlog y evidencias de cierre se mantienen en [INVENTARIO_REFORMA.md](INVENTARIO_REFORMA.md). UD1 no se declarará cerrada hasta completar y verificar ese inventario.
