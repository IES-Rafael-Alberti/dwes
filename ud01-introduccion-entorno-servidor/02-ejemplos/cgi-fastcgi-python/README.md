# Nginx, FastCGI y un script Python CGI

Demo opcional de UD01 para observar un segundo mecanismo de ejecución web,
antes de comparar con el proceso Java persistente de Hello Server. No introduce
un tercer instrumento de evaluación: la [tarea breve](../../03-ejercicios/04-explorar-cgi-fastcgi/README.md)
es práctica formativa.

## Requisitos

- Docker con `docker compose` disponible y acceso inicial para descargar las
  imágenes y paquetes.
- Puerto local 8081 libre. Hello Server usa normalmente el 8080.

Desde esta carpeta:

```bash
chmod +x cgi/scripts/saludo.py
docker compose up --build -d
docker compose ps
```

Abre <http://localhost:8081/> y luego pulsa el enlace al saludo CGI. Prueba
también <http://localhost:8081/cgi-bin/saludo.py?nombre=Ada>. Para ver estado,
cabeceras y contenido desde terminal:

```bash
curl -i 'http://localhost:8081/'
curl -i 'http://localhost:8081/cgi-bin/saludo.py?nombre=Ada'
```

Para detener y retirar estos contenedores:

```bash
docker compose down
```

## Qué está ocurriendo

```text
navegador --HTTP--> Nginx --FastCGI--> fcgiwrap --CGI--> script Python
                                                      <-- HTML generado --
```

- Nginx sirve `/` desde `web/index.html` y envía **solo**
  `/cgi-bin/saludo.py` al servicio `cgi` mediante `fastcgi_pass`.
- `fcgiwrap` es el proceso FastCGI que permanece preparado. Recibe parámetros
  como `SCRIPT_FILENAME` y `QUERY_STRING` y lanza el script Python como CGI
  **en cada petición**. En este ejemplo, Python no es un trabajador FastCGI
  persistente: no llamar a esta demo «aplicación Python FastCGI».
- El script escribe primero `Content-Type`, una línea en blanco y después el
  HTML. El PID mostrado permite inspeccionar qué proceso atendió la petición;
  si el sistema reutiliza un PID, eso por sí solo no demuestra que sea el mismo
  proceso.
- `cgi/scripts/` está montado desde el equipo en el contenedor en modo lectura.
  Editar `saludo.py` **en el equipo** permite volver a cargar la página sin
  reconstruir la imagen. No hace falta editar dentro del contenedor.

## Si no funciona

- `docker compose ps` y `docker compose logs cgi nginx` muestran el estado.
- Si el navegador muestra 502, espera unos segundos y recarga: Nginx puede
  arrancar antes de que `fcgiwrap` empiece a escuchar. Si continúa, revisa los
  logs y el permiso ejecutable de `cgi/scripts/saludo.py`.
- Si el puerto 8081 está ocupado, cambia solo el **primer** número de
  `127.0.0.1:8081:80` en `compose.yaml` y utiliza el mismo puerto en la URL.

La página y el script están diseñados para observar el mecanismo, no como
modelo para desplegar un servicio público. El salto a Nginx/CGI/FastCGI no
cambia el objetivo del módulo: el backend principal será Java con Spring Boot.
