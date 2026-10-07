---
title: "UD01 — ¿Quién atiende la petición?"
subtitle: "03 · Servidores web, proxies y contenedores"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## Una pregunta por componente · 3 min

**¿Qué responsabilidad asume?** No clasifiques por el nombre del producto.

```text
cliente --HTTP--> entrada web --> aplicación --> respuesta
                    ¿quién dirige?  ¿quién ejecuta Java?
```

- La misma máquina puede alojar varios componentes.
- Un mismo producto puede desempeñar más de un papel.

*Base: [03 — «Responsabilidades»](../03-infraestructura-web.md).*

## ¿Servidor web, proxy o balanceador? · 5 min

- **Servidor web:** recibe HTTP y puede entregar archivos.
- **Proxy inverso:** recibe peticiones públicas y las dirige a otros servicios.
- **Balanceador:** reparte peticiones entre instancias.

Nginx puede servir archivos **y** actuar como proxy: son funciones, no
etiquetas exclusivas.

**Pregunta:** si `/static` sirve una imagen y `/api` va a Java, ¿dónde se
ejecuta el controlador?

*Base: [03 — «Responsabilidades»](../03-infraestructura-web.md).*

## Un despliegue con proxy inverso · 4 min

```text
         HTTPS
cliente --------> Nginx
                    |-- /static --> archivos
                    `-- /api ----> Spring Boot :8080
```

El proxy puede terminar TLS y encaminar tráfico. **Validar los datos y
autorizar operaciones sigue siendo responsabilidad de la aplicación.**

*Base: [03 — «Flujo habitual con proxy inverso»](../03-infraestructura-web.md).*

## Tomcat externo frente a embebido · 5 min

| Externo | Embebido en Spring Boot |
|---|---|
| Se instala el contenedor | Arranca con la aplicación |
| Puede alojar varias aplicaciones | Aplicación y servidor forman una unidad |
| Ciclo de vida propio | Se ejecuta como proceso Java |

**Ambos** pueden recibir HTTP y alojar código servidor. «Embebido» no
significa «sin servidor» ni impide usar Nginx por delante.

*Base: [03 — «Tomcat externo o embebido»](../03-infraestructura-web.md).*

## Lo que una cabecera NO demuestra · 3 min

`Server: nginx` podría describir solo el proxy que respondió al cliente.

- `Server` y `X-Powered-By` pueden faltar o haberse modificado.
- Una CDN o un proxy pueden ocultar la aplicación real.
- **No deduzcas «el backend es Nginx» de una sola cabecera.**

*Base: [03 — «Señales que no demuestran la arquitectura»](../03-infraestructura-web.md).*

## Dibuja dos caminos · 6 min

1. **Desarrollo:** navegador → Spring Boot con Tomcat embebido.
2. **Despliegue:** navegador → Nginx → dos instancias Spring Boot.

En cada dibujo señala **dónde termina TLS**, **dónde se ejecuta Java** y
**quién puede distribuir las peticiones**.

Si dudas, etiqueta primero responsabilidades y después productos.

*Base: [03 — «Comprobación»](../03-infraestructura-web.md).*

## Cierre · 2 min

**Una frase para llevarse:** servidor web, proxy, contenedor y aplicación no
son sinónimos; describen responsabilidades que pueden combinarse.

**Siguiente parada:** elegir tecnologías para construir la respuesta, sin
cambiar todavía de tema a los detalles del protocolo HTTP.
