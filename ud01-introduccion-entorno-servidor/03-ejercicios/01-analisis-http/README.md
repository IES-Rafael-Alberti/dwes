# Laboratorio HTTP y ejecución cliente-servidor

## Objetivo

Observar el recorrido real de una petición contra Hello Server, interpretar mensajes HTTP sin exponer información sensible y relacionar la evidencia con los mecanismos de ejecución estudiados en UD1.

## Requisitos

- Java 25.
- [Hello Server](../../02-ejemplos/hello-server/README.md).
- HTTPie o cURL.
- Navegador con herramientas de desarrollo.
- Editor de Markdown.

No se inspeccionan cuentas personales ni sitios públicos arbitrarios. El objetivo controlado es `http://localhost:8080`.

## Preparación

Desde `02-ejemplos/hello-server/`:

```bash
./mvnw test
./mvnw spring-boot:run
```

Mantén el servidor activo y trabaja desde otra terminal.

## 1. Tres representaciones

Ejecuta con HTTPie:

```bash
http --print=HBhb GET http://localhost:8080/
http --print=HBhb GET http://localhost:8080/api/hello
http --print=HBhb GET http://localhost:8080/health
```

O con cURL:

```bash
curl --include http://localhost:8080/
curl --include http://localhost:8080/api/hello
curl --include http://localhost:8080/health
```

Para cada respuesta registra:

- método y destino;
- estado;
- `Content-Type`;
- tipo de representación;
- método Java que produjo la respuesta.

Explica por qué `/` devuelve HTML y `/api/hello` devuelve JSON aunque ambos se implementen en el mismo controlador.

## 2. Solicitud, respuesta y errores

Obtén una traza detallada de `GET /api/hello` con `http --verbose` o `curl --verbose`. Conserva solo los campos necesarios.

Después ejecuta:

```bash
curl --head http://localhost:8080/api/hello
curl --include --request OPTIONS http://localhost:8080/api/hello
curl --include http://localhost:8080/no-existe
```

Responde:

1. ¿Qué diferencia hay entre método, destino, cabeceras y contenido?
2. ¿Qué información aporta cada estado recibido?
3. ¿Devuelve `OPTIONS` una cabecera `Allow`? Describe únicamente la evidencia observada; no presupongas que todos los servidores responden igual.
4. ¿Qué información interna sería peligroso incluir en una respuesta de error?

## 3. Navegador y frontera de ejecución

Abre `http://localhost:8080/` y utiliza la pestaña **Red**:

1. Localiza la petición del documento HTML.
2. Identifica qué ocurrió en el navegador y qué ocurrió en Java.
3. Explica por qué modificar el HTML en las herramientas del navegador no modifica el código servidor.
4. Indica una validación que podría mejorar la experiencia en cliente, pero que el servidor tendría que repetir.
5. Justifica tres ventajas y dos costes de generar contenido dinámico en servidor.

## 4. Del socket al controlador

Dibuja el recorrido de `GET /health`:

```text
cliente -> Tomcat embebido -> DispatcherServlet -> HelloController -> conversor JSON -> respuesta
```

Compara este proceso persistente con CGI y PHP-FPM/FastCGI. Debes identificar qué proceso permanece activo y qué componente ejecuta el código de aplicación.

Aclara por qué `Server` o `X-Powered-By` serían como máximo indicios y no una prueba fiable de toda la arquitectura.

## 5. Selección tecnológica

Compara Spring Boot y Laravel usando al menos estos criterios:

- lenguaje y runtime;
- mecanismo habitual de ejecución;
- tipado y herramientas de prueba;
- despliegue y operación;
- papel didáctico dentro del módulo.

Concluye cuándo elegirías cada uno. No se acepta "es mejor" sin contexto ni criterios.

## 6. QUERY

Lee [RFC 10008](https://www.rfc-editor.org/rfc/rfc10008.html) y el apartado QUERY de la guía HTTP. No ejecutes QUERY contra Hello Server: este laboratorio no ha demostrado soporte en toda la cadena.

Explica:

1. Qué problema resuelve frente a GET y POST.
2. Por qué es seguro e idempotente.
3. Por qué requiere contenido y `Content-Type` coherentes.
4. Qué tendría que verificarse antes de adoptarlo en una aplicación real.

## Seguridad de la entrega

Aplica la [guía de seguridad HTTP](../../06-seguridad/README.md):

- redacta cookies, tokens y `Authorization` como `<REDACTED>`;
- no incluyas rutas personales, variables de entorno ni pestañas ajenas;
- usa texto copiable en lugar de capturas cuando sea posible;
- si expones un secreto real, revócalo; borrar la captura no basta.

## Entrega

Entrega un único `entrega.md` basado en [plantilla-entrega.md](plantilla-entrega.md). Las trazas deben ser breves, textuales y sanitizadas.

Consulta [rubrica.md](rubrica.md) y [ra-ce.md](ra-ce.md) antes de empezar.

## Política de IA

En esta primera unidad no se permite usar IA generativa para resolver o redactar el laboratorio. El objetivo es construir una línea base propia sobre HTTP y ejecución servidor que permita evaluar críticamente estas herramientas más adelante.
