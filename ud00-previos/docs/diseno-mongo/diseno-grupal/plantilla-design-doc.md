# Design Document: [Nombre del proyecto]

## Integrantes
- Nombre 1
- Nombre 2
- Nombre 3

## 1. Descripción del dominio
[2-3 párrafos explicando el sistema]

## 2. Patrones de acceso

| # | Operación | Frecuencia | Colecciones implicadas | Datos devueltos |
|---|-----------|------------|------------------------|-----------------|
| 1 | Ver dashboard de usuario | Alta | usuarios, proyectos | nombre, email, #proyectos, #tareas pendientes |

## 3. Decisiones embed vs reference

| Relación | Tipo | Decisión | Justificación |
|----------|------|----------|---------------|
| [entidad A] - [entidad B] | 1:N | Embed/Ref | [Explicar por qué basado en patrones de acceso] |

## 4. Schemas

### Colección: [nombre]

```json
{ "$jsonSchema": { ... } }
```

Justificación: ...

## 5. Índices

| Colección | Índice | Tipo | Justificación (ESR) |
|-----------|--------|------|---------------------|
| ... | `{ campo: 1 }` | simple | ... |

## 6. Migraciones

### Migración 1: [nombre]
- Script up: ...
- Script down: ...
- Idempotente: [sí/no, cómo]

## 7. Seguridad

| Usuario | Rol | BD | Justificación |
|---------|-----|----|--------------|
| ... | readWrite | ... | ... |

## 8. Anti-patrones evitados

- [ ] Documento gigante (más de 50 campos)
- [ ] Array infinito
- [ ] SQL mental (demasiados $lookup)
- [ ] Sin validación
- [ ] Otros: ...
