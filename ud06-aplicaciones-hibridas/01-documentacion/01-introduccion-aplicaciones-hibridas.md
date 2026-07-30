# Introducción a las aplicaciones híbridas

## El problema

Una aplicación web convencional gestiona datos propios: el equipo docente crea las tablas, escribe las consultas y controla el esquema. Cuando la aplicación necesita información que no genera —catálogos externos, datos abiertos, APIs de terceros— surgen preguntas que el desarrollo a medida no resuelve:

- ¿De dónde vienen estos datos? ¿Son fiables?
- ¿Qué licencia tienen? ¿Podemos redistribuirlos?
- ¿Qué ocurre si la fuente deja de estar disponible?
- ¿Cómo actualizamos nuestra copia sin romper lo que ya funciona?
- ¿Cómo distinguimos, ante un fallo, si el error es nuestro o del proveedor?

Este conjunto de problemas es el objeto de RA9 y de UD6.

## ¿Qué es una aplicación híbrida?

Una aplicación que **combina recursos propios con recursos externos** —código, datos, servicios— de forma controlada y verificable. No se limita a consumir una API, sino que integra, transforma y persiste información de múltiples procedencias, registrando su origen y las condiciones de uso.

Características esenciales:

- **Heterogeneidad de fuentes**: al menos dos formatos o protocolos distintos (API REST + fichero versionado)
- **Procedencia explícita**: cada dato registra su origen y licencia
- **Idempotencia**: ejecutar la ingesta múltiples veces produce el mismo resultado
- **Resiliencia**: la aplicación tolera fallos del proveedor externo
- **Verificabilidad**: las pruebas no dependen de la disponibilidad de terceros

## Procedencia de fuentes

| Fuente | Tipo | Formato | Licencia | Actualización |
|--------|------|---------|----------|---------------|
| Open Library Search API | Remota (REST) | JSON | Internet Archive: sin copyright nuevo; derechos de contribuciones/jurisdicción variables | Consulta bajo demanda con caché |
| Wikidata (subconjunto educativo) | Versionada en repo | CSV o JSON | CC0 | Commit del repositorio |

Las fuentes se seleccionaron y documentaron en la Fase 0. El contrato completo con restricciones de uso, modelo normalizado, reglas de idempotencia y semántica de fallos está en `02-contrato-fase-0.md`.

## Fuera de alcance

Esta unidad **no** cubre:
- RAG, vector stores, MCP ni agentes de IA
- Hacer de la IA el eje central de la aplicación
- Entrenamiento o ajuste de modelos
- Una unidad completa de Spring AI o FastAPI

La IA aparece únicamente como ampliación opcional P3: una llamada a un chat model mediante Spring Boot + Spring AI. La aplicación debe funcionar completa sin ella.

## Ruta incremental

```
P0  → Contrato, publicación, planificación (completada)
P1  → Ejemplo ejecutable con Spring Boot
    ├── Cliente HTTP con WebClient
    ├── Ingesta y normalización
    ├── Persistencia idempotente
    ├── Pruebas offline
    └── Documentación
P2  → Práctica y proyecto evaluable
P3  → Ampliación opcional (Spring AI, si aplica)
```

Cada fase produce material publicable. No se escribe código no verificable ni se incluyen fuentes sin licencia compatible.
