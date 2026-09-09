# Enunciado trabajo grupal

**Título**: Proyecto de diseño MongoDB

**Formato**: Grupos de 3-4 alumnos

**Duración**: 2 semanas (trabajo autónomo, 2h día de dudas)

## Requisitos

Elegid un dominio, aprobado por el profesor, de esta lista sugerida:

- Gestión de incidencias / helpdesk
- Sistema de reservas (hotel / restaurante)
- Red social mínima (posts, likes, follows, comments)
- Plataforma de cursos online
- Sistema de inventario / logística
- Gestor documental con versionado

Debéis realizar el proceso completo de diseño justificado:

1. **Análisis de acceso**: identificar los 5-6 patrones de acceso principales.
2. **Decisiones embed vs reference**: tabla con cada relación y justificación.
3. **Schemas**: `$jsonSchema` para cada colección.
4. **Índices**: lista justificada con ESR rule.
5. **Migraciones**: 2 scripts de migración con `up` y `down`.
6. **Seguridad**: usuarios y roles para el sistema.
7. **Anti-patrones**: identificar los anti-patrones evitados.

## Entregable

`diseno-mongo-grupoX.md` siguiendo la plantilla proporcionada.

## Criterios de evaluación

Ver `rubrica.md` y `ra-ce.md` en este directorio para el desglose detallado de criterios y la alineación con los RA/CE del módulo. Los grupos deben autoevaluarse antes de la defensa oral.

## Entregables

- Documento de diseño `diseno-mongo-grupoX.md` (utilizando la plantilla provista).
- Scripts de migración en JS (idempotentes, con rollback `up` / `down`).
- Declaración individual de uso de IA por parte de cada integrante (ver plantilla en `00-recursos-comunes/plantillas/`).

## Política de IA

| Aspecto | |
|---------|-|
| Uso de IA permitido | Sí, como asistente técnico para diseño inicial o corrección de esquemas y scripts. |
| Declaración obligatoria | Sí, individual de cada miembro (no por grupo). |
| Herramientas permitidas | ChatGPT, Claude, Gemini, GitHub Copilot. |
| Qué NO está permitido | Generar de forma totalmente automatizada la defensa oral, el análisis de accesos o los esquemas completos sin justificación manual ni control técnico de los resultados. |

