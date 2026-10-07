---
title: "UD01 — Del servidor web a la respuesta HTTP"
subtitle: "Mecanismos, arquitectura y primera traza"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## La ruta de hoy · 2 min

1. ¿Quién recibe la petición y quién ejecuta el código?
2. CGI, FastCGI y proceso persistente.
3. ¿Qué envía HTTP y qué devuelve el servidor?
4. Verlo **de verdad** con HTTPie y Hello Server.

**Meta:** localizar cuatro cosas en una traza: método, estado, `Content-Type`
y contenido. Si nos falta tiempo, paramos tras una petición completa.

## Una petición, varias responsabilidades · 4 min

```text
navegador --HTTP--> servidor web / proxy
                           |
                           v
                      runtime + aplicación
                           |
                           v
                     respuesta HTTP
```

**Pregunta:** ¿es el navegador quien ejecuta nuestro código Java?

*Base: [02 — Mecanismos](../02-mecanismos-ejecucion-servidor.md), «Modelo general».*

## CGI no es FastCGI · 5 min

- **CGI:** ejecutar un script como proceso para atender una petición.
- **FastCGI:** comunicación con procesos que ya están preparados y se
  reutilizan; evita arrancar **ese** proceso en cada petición.
- Nginx + `fcgiwrap` + Python: `fcgiwrap` permanece; **el script CGI Python se
  lanza por petición**. PHP-FPM es otro caso: mantiene trabajadores PHP.

**Pregunta:** ¿qué proceso permanece vivo en cada caso?

*Base: [02 — Mecanismos](../02-mecanismos-ejecucion-servidor.md), tabla y
[demo CGI/FastCGI](../../02-ejemplos/cgi-fastcgi-python/README.md).*

## Un servidor Java que permanece · 4 min

```text
navegador --HTTP--> Tomcat embebido --ruta--> controlador Spring
                                      <- HTML o JSON -
```

- La aplicación Java y Tomcat se inician y siguen atendiendo peticiones.
- No se arranca la JVM de nuevo cada vez que visitas una URL.

**Pregunta:** ¿qué se comparte entre peticiones y qué depende de cada una?

*Base: [02 — Mecanismos](../02-mecanismos-ejecucion-servidor.md), «Concurrencia y
estado» y [Hello Server](../../02-ejemplos/hello-server/README.md).*

## Tres decisiones que no son lo mismo · 4 min

| Pregunta | Ejemplo |
|---|---|
| ¿Cómo se invoca el código? | CGI / FastCGI / proceso persistente |
| ¿Cómo se organiza la aplicación? | Controlador y lógica en un mismo proyecto |
| ¿Dónde se despliega? | Detrás de Nginx, en otra máquina... |

**Un monolito puede usar un proceso persistente y vivir detrás de un proxy.**

*Base: [02 — «Mecanismo frente a arquitectura»](../02-mecanismos-ejecucion-servidor.md).*

## Nginx, Tomcat y Spring: ¿quién hace qué? · 5 min

- **Nginx:** puede recibir HTTP, servir ficheros o actuar como proxy.
- **Tomcat embebido:** recibe HTTP y ejecuta el entorno web Java.
- **Spring Boot:** configura la aplicación y llama a nuestro controlador.

El nombre de un producto no explica por sí solo toda la arquitectura.

*Base: [03 — Servidores web y contenedores](../03-infraestructura-web.md),
«Responsabilidades» y «Tomcat externo o embebido».*

## ¿Archivo existente o respuesta generada? · 4 min

| Petición | Respuesta |
|---|---|
| `/` a un fichero estático | HTML ya almacenado |
| `/` a Hello Server | HTML generado por Java |
| `/api/hello` | JSON generado a partir de datos Java |

**Interactivo ≠ generado en servidor.** El navegador puede ejecutar JavaScript
sobre un archivo estático.

*Base: [01 — Contenido dinámico](../01-cliente-servidor-y-contenido-dinamico.md)
y [04 — Integración](../04-tecnologias-e-integracion.md).*

## HTTP: mismo contrato, distinta representación · 4 min

- HTTP/1.1: el mensaje se puede leer como texto en estos ejemplos.
- HTTP/2 y HTTP/3: se transportan mediante formatos binarios.
- Conservan conceptos: **petición, método, estado, cabeceras, contenido**.

**Hoy leemos el intercambio HTTP/1.1; no necesitamos aprender el formato de
los frames.**

*Base: [05 — Versiones y mensajes HTTP](../05-versiones-y-mensajes-http.md).*

## Una petición en cuatro piezas · 4 min

```http
GET /api/hello HTTP/1.1
Host: localhost:8080
Accept: application/json

```

1. **GET:** intención; **/api/hello:** destino.
2. **Host** y **Accept:** cabeceras que envía el cliente.
3. Línea en blanco: separa cabeceras y contenido (aquí no hay cuerpo).

*Base: [05 — «Estructura de una petición»](../05-versiones-y-mensajes-http.md).*

## Una respuesta en tres piezas · 4 min

```http
HTTP/1.1 200 OK
Content-Type: application/json

{"message":"Hola desde el servidor"}
```

**200** indica el resultado; `Content-Type` dice cómo interpretar el **cuerpo**.
No confundas la URL `/api/hello` con el tipo del contenido.

*Base: [05 — «Estructura de una respuesta»](../05-versiones-y-mensajes-http.md).*

## HTTPie: una petición real · 15 min

Con [Hello Server](../../02-ejemplos/hello-server/README.md) ya arrancado:

```bash
http --verbose GET http://localhost:8080/api/hello
http --headers GET http://localhost:8080/
http --body GET http://localhost:8080/
http --verbose GET http://localhost:8080/no-existe
```

**Localiza:** petición, estado 200/404, `Content-Type` y cuerpo. Si no hay
HTTPie, `curl -i` muestra la respuesta y `curl -v` también la petición.

*Base: [Hello Server — «Ejecución»](../../02-ejemplos/hello-server/README.md),
[07 — Estados HTTP](../07-estados-http.md).*

## Salida: tres preguntas · 4 min

1. ¿Quién ejecutó el código Java: navegador, Tomcat, controlador...?
2. ¿Qué parte de la traza te dice si recibiste HTML o JSON?
3. ¿Qué cambia entre `GET /api/hello` y `GET /no-existe`?

**Después:** métodos HTTP y cabeceras en detalle; todavía no CRUD ni una API
REST completa.

*Para seguir: [06 — Métodos](../06-metodos-http.md), [07 — Estados](../07-estados-http.md)
y [08 — Cabeceras](../08-cabeceras-http-contenido-y-cache.md).*
