---
title: "UD01 — Elegir backend con criterio"
subtitle: "04 · Tecnologías, frameworks e integración"
author: "DWES · 2026/2027"
lang: es
aspectratio: 169
---

## No busques «el mejor lenguaje» · 3 min

Una elección depende de **problema, equipo, seguridad, despliegue y
mantenimiento**.

- Dos tecnologías pueden producir la misma respuesta.
- Sus costes de aprendizaje y operación no tienen por qué ser iguales.

**Pregunta:** ¿qué información pedirías antes de recomendar un backend?

*Base: [04 — «Criterios de selección»](../04-tecnologias-e-integracion.md).*

## Lenguaje, runtime y framework · 4 min

| Concepto | Pregunta útil | Ejemplo |
|---|---|---|
| Lenguaje | ¿Con qué escribo el código? | Java, PHP |
| Runtime | ¿Qué ejecuta el programa? | JVM, intérprete PHP |
| Framework | ¿Quién coordina el flujo? | Spring Boot, Laravel |

Una **biblioteca** aporta funciones que llamamos; el framework llama a
nuestro código en puntos previstos. Un SDK agrupa herramientas y APIs; un
CMS es un producto para gestionar contenidos.

*Base: [04 — «Vocabulario mínimo»](../04-tecnologias-e-integracion.md).*

## ¿Qué compra un framework? · 4 min

- Enrutamiento, configuración y ciclo de petición.
- Pruebas y convenciones compartidas.
- Seguridad, validación y acceso a datos: **ayudas**, no magia automática.

**Precio:** aprender sus reglas y mantener las dependencias. Sin entender
HTTP, usar un framework no arregla un diseño confuso.

*Base: [04 — «Framework frente a código propio»](../04-tecnologias-e-integracion.md).*

## Spring Boot y Laravel en DWES · 5 min

| Opción | Papel en este módulo | Coste a considerar |
|---|---|---|
| Java + Spring Boot | Backend principal | Más conceptos y estructura inicial |
| PHP + Laravel | Segundo framework completo | Entender convenciones y tipado dinámico |

.NET puede mostrarse como alternativa; Node.js ya aparece en el itinerario
MERN. **No es una clasificación absoluta de calidad.**

*Base: [04 — «Comparación orientativa del módulo»](../04-tecnologias-e-integracion.md).*

## ¿Qué devuelve el servidor? · 5 min

| Salida | Cómo se obtiene |
|---|---|
| HTML estático | Archivo existente |
| HTML dinámico | Código o plantilla + datos |
| JSON | Objetos o DTO serializados |
| Redirección | Estado + destino `Location` |

Una plantilla **representa** datos; no es la base de datos. Devolver JSON
**no** convierte automáticamente un endpoint en una API REST.

*Base: [04 — «Integración con lenguajes de marcas»](../04-tecnologias-e-integracion.md).*

## Dos minutos de decisión · 5 min

Una aplicación necesita mostrar HTML a personas y JSON a un cliente móvil.
El equipo conoce Java y necesita pruebas y despliegue reproducible.

**Por parejas:** proponed una opción y defended vuestra elección con:

1. Un criterio **técnico**.
2. Un criterio de **operación o mantenimiento**.
3. Una limitación real de la opción elegida.

*Base: [04 — «Criterios de selección»](../04-tecnologias-e-integracion.md).*

## Cierre · 2 min

**Elegir un framework no sustituye comprender qué ejecuta cada componente
ni qué representación recibe el cliente.**

Nos quedamos aquí: versiones, mensajes, métodos y estados HTTP pertenecen al
siguiente bloque de la unidad.
