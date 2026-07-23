# Cómo llega HTTP al código servidor

Un servidor web sabe recibir HTTP, pero no ejecuta por sí mismo cualquier lenguaje. Necesita un mecanismo que conecte la petición con el runtime y con el código de la aplicación. Las tecnologías han evolucionado para evitar crear un proceso costoso en cada petición y para gestionar concurrencia, aislamiento y despliegue.

## Modelo general

```text
cliente -> servidor web o proxy -> runtime/contenedor -> aplicación -> datos o servicios
```

Las piezas pueden vivir en el mismo proceso, en procesos separados o incluso en máquinas distintas. La arquitectura monolítica no obliga a colocar la base de datos dentro de la aplicación.

## Mecanismos principales

| Mecanismo | Cómo funciona | Ventaja | Coste o riesgo |
|---|---|---|---|
| CGI | Ejecuta un proceso por petición | Modelo simple y aislado | Crear procesos limita el rendimiento |
| FastCGI | Mantiene procesos preparados y reutilizables | Evita arrancar el runtime en cada petición | Requiere gestionar procesos y comunicación |
| Módulo del servidor | El runtime se carga dentro del servidor web | Comunicación directa | Menor aislamiento entre servidor y aplicación |
| Proceso persistente | La aplicación escucha peticiones continuamente | Buen control de concurrencia y estado interno | Hay que gestionar ciclo de vida y recursos |
| Contenedor de aplicaciones | Un runtime aloja componentes bajo un contrato común | Despliegue, configuración y servicios compartidos | Añade convenciones y complejidad |

PHP suele desplegarse detrás de un servidor web mediante PHP-FPM/FastCGI. Una aplicación Spring Boot suele mantener un proceso Java persistente con un servidor HTTP embebido. Ninguno de esos modelos es universalmente mejor: responden a ecosistemas y necesidades diferentes.

## Concurrencia y estado

Un proceso persistente atiende muchas peticiones durante su vida. Esto mejora el rendimiento, pero obliga a pensar en:

- datos compartidos entre hilos o tareas;
- conexiones y pools limitados;
- fugas de memoria;
- bloqueos y operaciones lentas;
- cierre ordenado y recuperación ante fallos.

El estado de un usuario no debe guardarse en variables globales improvisadas. Las sesiones, bases de datos, cachés y tokens tienen contratos específicos que se estudiarán más adelante.

## Mecanismo frente a arquitectura

No se deben confundir estas decisiones:

- **Mecanismo de ejecución**: cómo se invoca el código ante una petición.
- **Arquitectura de aplicación**: cómo se organizan responsabilidades dentro del código.
- **Topología de despliegue**: en qué procesos y máquinas se ejecutan los componentes.

Una aplicación monolítica puede ejecutarse como proceso persistente detrás de un proxy y consultar una base de datos remota. Son decisiones independientes.

## Comprobación

Compara PHP-FPM y una aplicación Spring Boot ejecutable:

1. Qué proceso recibe inicialmente HTTP.
2. Qué proceso ejecuta el código de negocio.
3. Qué componentes permanecen vivos entre peticiones.
4. Qué recurso agotado podría impedir atender nuevas peticiones.

Esta evidencia trabaja RA1.c.
