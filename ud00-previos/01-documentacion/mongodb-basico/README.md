# MongoDB básico

Material de iniciación recuperado del bloque de MongoDB de SBD y revisado para
servir como antesala al bloque de diseño de datos.

## Secuencia recomendada

1. [Conceptos básicos](01-conceptos-basicos.md): documentos, colecciones, BSON,
   tipos de datos y comparación con el modelo relacional.
2. [Comandos de la shell](02-comandos-shell.md): bases de datos, colecciones,
   operaciones CRUD, consultas, índices y agregaciones.
3. [Comandos con PyMongo](03-comandos-pymongo.md): conexión y operaciones CRUD
   desde Python.

Estos documentos son material de referencia. Antes de reutilizar comandos de
instalación o conexión hay que comprobar la versión de MongoDB y el entorno
oficial del curso; no se ha copiado el antiguo guion de instalación porque
contiene instrucciones dependientes de versiones ya obsoletas.

## Conexión con el bloque de diseño

Después de practicar documentos y consultas, continúa con [Diseño de bases de
datos en MongoDB](../diseno-mongo/README.md). La progresión es deliberada:
primero se aprende la sintaxis y el modelo documental, y después se decide qué
se embebe, qué se referencia y qué reglas debe garantizar la aplicación.
