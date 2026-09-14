# Hello Server

Ejemplo mínimo de ejecución en servidor para UD1. Una única aplicación Spring Boot genera HTML y JSON y expone una comprobación de salud. No es todavía una API REST completa.

## Requisitos

- Java 25.
- Git.
- Acceso inicial a Maven Central para descargar dependencias.

## Verificación

```bash
./mvnw --version
./mvnw test
```

La suite comprueba:

- `GET /` devuelve HTML generado en servidor;
- `GET /api/hello` devuelve JSON;
- `GET /health` devuelve `{"status":"UP"}`.

Línea base validada: OpenJDK 25.0.3, Spring Boot 4.0.5, Maven Wrapper 3.9.11 y 3 pruebas verdes.

## Ejecución

```bash
./mvnw spring-boot:run
```

En otra terminal:

```bash
http --print=HBhb GET http://localhost:8080/
http --print=HBhb GET http://localhost:8080/api/hello
http --print=HBhb GET http://localhost:8080/health
```

HTTPie muestra por separado la petición (`H`, `B`), la respuesta (`h`, `b`)
y sus cabeceras y cuerpos. Empieza por leer el intercambio completo de
`/api/hello`:

```bash
http --verbose GET http://localhost:8080/api/hello
```

Localiza en la salida:

- `GET /api/hello`: método y destino de la petición;
- `Host` y `Accept`: información enviada por el cliente;
- `HTTP/1.1 200 OK`: estado de la respuesta;
- `Content-Type: application/json`: formato del cuerpo;
- `{"message":"Hola desde el servidor"}`: representación devuelta.

Después compara:

```bash
http --headers GET http://localhost:8080/
http --body GET http://localhost:8080/
http --headers GET http://localhost:8080/api/hello
http --body GET http://localhost:8080/api/hello
```

Explica qué cambia entre HTML y JSON y qué información desaparece al pedir
solo cabeceras o solo cuerpo. Como alternativa equivalente puede usarse
`curl -i`, pero la evidencia de esta guía se plantea con HTTPie.

## Qué observar

| Endpoint | Código servidor | Representación |
|---|---|---|
| `/` | Construye una cadena HTML | `text/html` |
| `/api/hello` | Construye un mapa Java | JSON mediante serialización |
| `/health` | Informa del estado mínimo del ejemplo | JSON |

`@RestController` hace que el valor devuelto forme parte de la respuesta. En `/`, `produces` declara HTML; en los mapas, Spring selecciona JSON mediante el convertidor configurado.

## La misma petición desde el navegador

Abre `http://localhost:8080/`, pulsa `F12` y selecciona **Red**. Recarga la
página y abre la petición del documento. Comprueba en **Encabezados** el método,
la URL, el estado y `Content-Type`; en **Respuesta**, compara el HTML recibido
con lo que el navegador representa. A continuación visita
`http://localhost:8080/api/hello` y observa que el cuerpo cambia a JSON.

El navegador es otro cliente HTTP: no ejecuta `HelloController`; envía la
petición y luego interpreta la respuesta. Cambiar el HTML desde las
herramientas de desarrollo solo modifica la copia local de la representación,
no el código Java del servidor.

## Fuera de alcance

- base de datos;
- formularios y validación;
- diseño REST completo;
- OpenAPI;
- autenticación y autorización;
- Actuator y observabilidad de producción.

Esos contenidos aparecen en unidades posteriores. Añadirlos aquí ocultaría el objetivo: observar el recorrido desde HTTP hasta código servidor y de vuelta a una representación.
