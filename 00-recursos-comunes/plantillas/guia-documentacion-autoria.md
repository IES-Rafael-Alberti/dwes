# Documentación proporcional y autoría

La documentación demuestra decisiones y trazabilidad; no se entrega como
burocracia desconectada del código. Cada documento debe enlazar con commits,
issues, pruebas o artefactos que permitan comprobarlo.

## Proyecto de alcance amplio

Obligatorio para el proyecto final y para proyectos que definan un dominio propio
con varias reglas y persistencia:

1. Especificación: problema, alcance, actores y requisitos.
2. Backlog TDD: tareas, regla, prueba previa, evidencia, estado y decisiones.
3. Plan técnico: arquitectura, datos, migraciones y puesta en marcha.
4. Plan de pruebas: estrategia y casos correctos, límite y error.
5. README e issue de entrega: ejecución, pruebas, responsabilidades y trazabilidad.
6. Contrato API cuando se exponga una API o integración HTTP propia.

La máquina de estados solo es obligatoria si el dominio tiene transiciones
relevantes. La integración cliente-servidor es opcional; una colección REST es
suficiente para demostrar el contrato de DWES.

## Proyecto acotado

Para proyectos con pocas sesiones o alcance guiado, entregar como mínimo una
especificación breve, backlog TDD, plan de pruebas, README e issue de entrega.
El plan técnico y el contrato se integran en esos documentos cuando el enunciado
ya proporciona la arquitectura o no se crea una API propia.

## Práctica menor o incremento

No requiere un dossier separado. El README e issue de entrega deben identificar
integrantes, slices, regla, prueba, comando de ejecución, error demostrado y
revisión de pares. Añadir una tabla breve de backlog o responsabilidades basta.
Se exige contrato HTTP u OpenAPI solo si el incremento crea o modifica endpoints.
