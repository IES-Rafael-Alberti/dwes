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
curl -i http://localhost:8080/
curl -i http://localhost:8080/api/hello
curl -i http://localhost:8080/health
```

## Qué observar

| Endpoint | Código servidor | Representación |
|---|---|---|
| `/` | Construye una cadena HTML | `text/html` |
| `/api/hello` | Construye un mapa Java | JSON mediante serialización |
| `/health` | Informa del estado mínimo del ejemplo | JSON |

`@RestController` hace que el valor devuelto forme parte de la respuesta. En `/`, `produces` declara HTML; en los mapas, Spring selecciona JSON mediante el convertidor configurado.

## Fuera de alcance

- base de datos;
- formularios y validación;
- diseño REST completo;
- OpenAPI;
- autenticación y autorización;
- Actuator y observabilidad de producción.

Esos contenidos aparecen en unidades posteriores. Añadirlos aquí ocultaría el objetivo: observar el recorrido desde HTTP hasta código servidor y de vuelta a una representación.
