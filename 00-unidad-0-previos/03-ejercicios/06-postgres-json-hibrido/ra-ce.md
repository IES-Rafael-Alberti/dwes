# RA/CE — Ejercicio PostgreSQL JSONB Modelo Híbrido

## Resultados de Aprendizaje y Criterios de Evaluación

| RA | CE | Descripción | ¿Cómo se evidencia en esta tarea? |
|----|----|-------------|----------------------------------|
| **RA6** | **a** | Se han analizado las tecnologías que permiten el acceso mediante programación a la información disponible en almacenes de datos. | Comparar la idoneidad del modelo híbrido de PostgreSQL (tablas relacionales con columnas JSONB para atributos dinámicos) frente a un modelo puramente NoSQL en MongoDB. |
| **RA6** | **b** | Se han creado aplicaciones que establezcan conexiones con bases de datos. | Escribir el DDL del modelo híbrido, creando índices GIN sobre campos JSONB y triggers de validación en PL/pgSQL para garantizar la integridad estructural de los datos no estructurados. |
| **RA6** | **c** | Se ha recuperado información almacenada en bases de datos. | Escribir consultas SQL utilizando operadores avanzados de JSONB (`->`, `->>`, `?`, `@>`) para filtrar pedidos por atributos dinámicos. |
| **RA6** | **g** | Se han probado y documentado las aplicaciones web. | Ejecutar y documentar con `EXPLAIN ANALYZE` la eficiencia de las consultas sobre JSONB, comprobando el uso efectivo de los índices GIN frente a sequential scans. |

## Contribución a la nota

- Peso sobre la evaluación de la unidad: **10%**
- Tipo: Individual
- Entrega: Script SQL de creación, trigger, inserciones y consultas + informe `explicacion.md` + declaración de IA.
