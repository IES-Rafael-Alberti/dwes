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

| Criterio | Puntos | Qué se espera | Autoevaluación |
|---|---:|---:|---|
| Justificación de decisiones | 2 | No basta decir "embebo": hay que explicar por qué | ☐ |
| Calidad técnica de schemas e índices | 2 | Validación real, ESR rule, justificación | ☐ |
| Migraciones | 1.5 | Idempotentes, con rollback, bien escritas | ☐ |
| Seguridad | 1 | Roles correctos, mínimo privilegio | ☐ |
| Anti-patrones | 1 | Identificados correctamente | ☐ |
| Documentación y claridad | 1.5 | Explicaciones claras, ejemplos | ☐ |
| Defensa oral | 1 | En día de dudas, cada grupo defiende 5 min | — |

Los grupos deben **autoevaluarse marcando la columna** antes de entregar. El profesor contrastará la autoevaluación con su propia evaluación. Coincidir suma puntos; desviarse demasiado (sobrevalorarse o infravalorarse) resta 0.5 en cada criterio.
