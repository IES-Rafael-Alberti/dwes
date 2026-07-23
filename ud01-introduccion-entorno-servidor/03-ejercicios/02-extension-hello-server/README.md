# Extensión TDD de Hello Server

## Objetivo

Demostrar que el entorno es reproducible y realizar un cambio HTTP mínimo guiado por una prueba. La actividad no evalúa todavía diseño REST, persistencia ni seguridad de acceso.

## Punto de partida

Copia o clona [Hello Server](../../02-ejemplos/hello-server/README.md) en tu espacio de trabajo. No trabajes dentro del ejemplo compartido.

Comprueba la línea base:

```bash
./mvnw --version
./mvnw test
```

Debes obtener Java 25, Maven Wrapper 3.9.11 y tres pruebas verdes.

## Incremento 1 - prueba roja

Añade una prueba MockMvc para `GET /api/info` que exija:

- estado `200`;
- `Content-Type` compatible con JSON;
- `application` igual a `hello-server`;
- `purpose` igual a `observe-server-execution`.

Ejecuta solo la prueba nueva y conserva la salida RED textual y breve. El fallo debe deberse a que el endpoint aún no existe, no a un error de compilación.

## Incremento 2 - implementación mínima

Implementa `GET /api/info` en `HelloController` devolviendo exactamente el contrato probado. No añadas dependencias, capas, base de datos ni configuración adicional.

Ejecuta:

```bash
./mvnw test
```

Las cuatro pruebas deben quedar verdes.

## Incremento 3 - evidencia HTTP

Arranca la aplicación y ejecuta:

```bash
curl --include http://localhost:8080/api/info
```

Explica el recorrido desde Tomcat embebido hasta el conversor JSON y por qué el mapa Java no se envía por la red como un objeto Java.

## Entrega

- commit RED con la prueba;
- commit GREEN con la implementación mínima;
- `README.md` breve con versiones, comandos, salida de pruebas y explicación del recorrido;
- salida HTTP textual y sanitizada.

No entregues `target/`, archivos del IDE ni capturas de toda la pantalla.

## Política de IA

No se permite IA generativa. Esta actividad establece la línea base necesaria para evaluar herramientas de IA de forma crítica en unidades posteriores.

Consulta [rubrica.md](rubrica.md) y [ra-ce.md](ra-ce.md).
