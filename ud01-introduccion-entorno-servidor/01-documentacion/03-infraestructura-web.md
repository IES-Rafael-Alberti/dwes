# Servidores web, proxies y contenedores

Los nombres se parecen, pero no son sinónimos. La forma más segura de distinguir cada componente es preguntar qué responsabilidad asume y qué protocolo utiliza para comunicarse con el siguiente.

## Responsabilidades

| Componente | Responsabilidad principal | Ejemplos |
|---|---|---|
| Servidor web | Recibir HTTP, servir archivos y aplicar configuración web | Nginx, Apache HTTP Server, Caddy |
| Proxy inverso | Recibir peticiones públicas y dirigirlas a servicios internos | Nginx, HAProxy, Traefik |
| Balanceador | Repartir tráfico entre varias instancias | HAProxy, balanceadores cloud |
| Contenedor servlet | Ejecutar aplicaciones Java basadas en el contrato Servlet | Tomcat, Jetty |
| Servidor embebido | Vivir dentro del proceso de la aplicación | Tomcat embebido en Spring Boot |
| Servidor de aplicaciones | Añadir servicios empresariales estandarizados además del contenedor web | WildFly, Payara |

Un producto puede asumir varias funciones. Nginx puede servir archivos y actuar como proxy. Tomcat puede recibir HTTP directamente y ejecutar servlets. La tabla clasifica responsabilidades, no limita productos.

## Flujo habitual con proxy inverso

```text
Internet
   |
HTTPS
   v
proxy inverso
   |-- /static -> archivos
   |-- /api    -> aplicación Spring Boot :8080
   `-- /admin  -> otro servicio :8090
```

El proxy puede terminar TLS, limitar tamaños, añadir cabeceras, registrar accesos y ocultar la topología interna. La aplicación sigue siendo responsable de validar entradas y autorizar operaciones.

## Tomcat externo o embebido

Con un Tomcat externo se instala el contenedor y se despliegan aplicaciones dentro de él. Con Spring Boot, el contenedor suele empaquetarse con la aplicación y arranca desde su `main`.

| Externo | Embebido |
|---|---|
| El contenedor tiene ciclo de vida propio | Aplicación y servidor forman una unidad desplegable |
| Puede alojar varias aplicaciones | Cada servicio suele ejecutar su instancia |
| Configuración central compartida | Configuración versionada con la aplicación |
| Despliegue tradicional mediante artefactos | Ejecución directa con `java -jar` |

El modelo embebido simplifica la reproducibilidad, pero no elimina la necesidad de proxy, TLS, límites y observabilidad en producción.

## Señales que no demuestran la arquitectura

Cabeceras como `Server` o `X-Powered-By` pueden faltar, estar modificadas o describir solo el primer intermediario. Una CDN puede ocultar completamente la aplicación real. Sirven como indicios, no como prueba del lenguaje o mecanismo de ejecución.

## Comprobación

Dibuja dos despliegues válidos para una aplicación Spring Boot:

1. Acceso directo al servidor embebido durante desarrollo.
2. Acceso de producción mediante proxy inverso y dos instancias.

Etiqueta dónde se termina TLS, dónde se ejecuta Java y dónde se balancea tráfico. Esta evidencia trabaja RA1.d.
