# Ejercicio 01 — Análisis de protocolo HTTP y Modelo de Ejecución

## Objetivo

Consolidar los conceptos del protocolo HTTP y diferenciar de forma práctica los modelos de ejecución cliente-servidor, así como la identificación de tecnologías de backend.

## Requisitos técnicos

- Herramientas de consola: `httpie` (recomendado por su salida coloreada y sintaxis amigable, comando `http`) o `curl` / `netcat` como alternativas clásicas.
- Navegador web con Herramientas de Desarrollador (Chrome, Firefox o Edge).
- Un editor de texto para redactar el documento final en formato Markdown.

## Tareas

### 1. Interacción HTTP mediante consola (HTTPie / cURL)

Utiliza la consola para lanzar una petición HTTP básica a un sitio público de tu elección (por ejemplo: `https://httpbin.org/headers` o `https://www.google.com`):

1. Envía una petición `GET` usando `http -v` (con HTTPie) o `curl -v` (con cURL) para ver tanto la solicitud como la respuesta con todas sus cabeceras.
2. Identifica y explica en tu entrega el propósito de al menos 3 cabeceras de solicitud y 3 cabeceras de respuesta que hayas capturado.
3. Envía una petición `OPTIONS` (ej. `http OPTIONS https://httpbin.org/get` o `curl -X OPTIONS -v ...`) e investiga qué métodos están permitidos.
4. **Reflexión sobre el nuevo método QUERY**: Explica teóricamente qué ventajas aporta el método QUERY (aprobado en junio de 2026) frente a una consulta masiva usando GET con parámetros en la URL o el uso de POST.

### 2. Inspección del navegador y Modelo de Ejecución

Abre tu navegador, entra en una web dinámica (por ejemplo, un periódico o una tienda online) y abre las Herramientas de Desarrollador (pestaña **Red** / **Network**):

1. Recarga la página y selecciona la primera petición (el documento HTML principal). Captura sus cabeceras.
2. Explica qué ocurre desde que pulsas "Enter" hasta que se renderiza el HTML. ¿Qué parte se ejecuta en el servidor y qué parte se ejecuta en tu navegador?
3. Sabiendo que el navegador puede ejecutar JavaScript (JS) en cliente, ¿por qué es necesaria la generación dinámica de páginas en el servidor? Enumera 3 ventajas críticas.
4. Intenta identificar qué tecnología de backend (lenguaje o servidor de aplicaciones) está detrás de la web elegida buscando cabeceras como `Server`, `X-Powered-By` o mediante cookies típicas de sesión (como `JSESSIONID`, `PHPSESSID`, `ASP.NET_SessionId`).

## Entregables

- Documento `entrega.md` que contenga las respuestas a los apartados anteriores, las trazas de cabeceras HTTP capturadas y tus explicaciones técnicas.
- Declaración de uso de IA cumplimentada (ver plantilla en `00-recursos-comunes/plantillas/`).

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, como soporte para buscar información sobre cabeceras HTTP específicas o depurar comandos `http` (HTTPie) o `curl`. |
| Declaración obligatoria | Sí. |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot. |
| Qué NO está permitido | Generar de forma totalmente automatizada las reflexiones del modelo de ejecución cliente-servidor o copiar explicaciones teóricas sin haber hecho la inspección real de cabeceras en consola. |

## Evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio.
