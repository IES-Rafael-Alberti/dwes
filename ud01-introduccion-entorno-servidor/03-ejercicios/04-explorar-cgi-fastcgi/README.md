# Tarea breve: de Nginx al script Python

Práctica formativa de UD01 (30-40 minutos), tras estudiar
[cómo llega HTTP al código servidor](../../01-documentacion/02-mecanismos-ejecucion-servidor.md).
No añade una nota independiente ni modifica la evaluación de la unidad.

## 1. Observar (10 minutos)

Arranca la [demo Nginx y CGI](../../02-ejemplos/cgi-fastcgi-python/README.md).
En el navegador compara `/` con `/cgi-bin/saludo.py?nombre=Ada`. Repite la
petición cambiando el nombre y examina ambas con `curl -i` o la pestaña Red.

Anota: qué recibió el navegador, qué parte se sirvió de un fichero y qué parte
produjo Python, así como `Content-Type` y el estado HTTP de cada respuesta.

## 2. Modificar (15 minutos)

Edita `02-ejemplos/cgi-fastcgi-python/cgi/scripts/saludo.py` **desde tu
equipo**. Añade un parámetro opcional `tema` en la URL: al abrir
`/cgi-bin/saludo.py?nombre=Ada&tema=HTTP`, el HTML debe mostrar también
«Tema: HTTP». Si no se envía `tema`, debe mostrar «Tema: sin especificar».
Después recarga sin reconstruir los contenedores.

El valor mostrado procede de la petición; antes de incluirlo en HTML, trátalo
como haces con `nombre` (longitud acotada y `html.escape`). No es necesario
construir un framework ni conocer Python en profundidad: basta entender el
diccionario `parametros` y la respuesta que ya genera el starter.

## 3. Explicar (10 minutos)

En un breve `entrega.md` (máximo una página), incluye:

1. Un diagrama con **navegador → Nginx → fcgiwrap → script Python → respuesta**
   y el protocolo/interfaz de cada tramo.
2. La URL con `nombre` y `tema`, el estado y el `Content-Type` observados.
3. La línea o líneas que modificaste y qué cambia al variar `tema`.
4. Qué componente permanece activo entre peticiones y cuál se ejecuta de
   nuevo; comparación en dos frases con Hello Server.

Si el profesor pide entrega en Moodle, adjunta `entrega.md` y `saludo.py`
modificado; **no** exportes la imagen ni entregues carpetas de Docker. Se
valora la explicación del recorrido más que escribir muchas líneas.
