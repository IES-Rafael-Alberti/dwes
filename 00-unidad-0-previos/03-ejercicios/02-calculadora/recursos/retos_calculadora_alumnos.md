---
title: "Retos calculadora"
author: 
date:
output:
  pdf_document:
    toc: true
    toc_depth: 2
    number_sections: false
    latex_engine: xelatex
fontsize: 11pt
geometry: margin=1.5cm
header-includes:
  - \renewcommand{\contentsname}{Índice de contenidos}
---

## Retos para la calculadora

### Propuestas de:

#### César

-   Agregar una función para reiniciar la calculadora
-   Hacer que la calculadora pueda realizar una operación utilizando el resultado de una
-   Operación con otro número
-   Hacer una función para limpiar la pantalla sin eliminar el resultado

Explicación de cada reto:

1.  Añadir un comando reset que se encargue de reiniciar la calculadora

2.  Reutilizar el resultado de modo que puedas seguir operando con el

3.  Añadir un comando clear que limpia la consola sin salir del programa y borrar el resultado (sin reiniciar)

#### Daniel Montes Iglesias

Retos para la calculadora:

-   Añadir la tangente para poder calcularla también.

-   Poder calcular log() con distintas bases

-   Reutilizar los resultados en otras operaciones.

-   Arreglar y mejorar los errores mostrados.

-   Cálculo de raíces cuadradas

#### Alejandro Borrego, Francisco Alba, Adrián Díaz, Sergio Aragón y Rocío Luque

1.  Reto 1. Depuración: Errores más descriptivos. Detecta y corrige los fallos en los mensajes de error para que se correspondan con un aviso descriptivo del problema real. Ejemplo de salida actual:

``` bash
> hola( + 1
Error: Se esperaba ')' tras argumento (pos 9)
> hola( + 1)
Error: Función no soportada: hola
```

2.  Reto 2. Modificación: Log de operaciones en memoria. Opción en el menú para listar las operaciones realizadas durante la ejecución actual del programa.
3.  Reto 3. Modificación: Usar resultado anterior. Añadir la posibilidad de utilizar la solución de la operación anterior como miembro de la siguiente operación

#### Alejandro Bravo, Alfonso Castejón, Ezequiel Ortega, Jesús López y Alberto Rodríguez

Calculadora Retos

Reto 1: ¿Qué hace la función peek()?

Reto 2: Calculadora para despejar incógnitas.

Reto 3: Guardar el último número calculado en memoria ‘Ans’.

Retos opcionales

Reto 4: ¿Qué hace la función number()?

Reto 5: Inclusión de la tangente

Reto 6: ¿Qué hace la clase Evaluator()?

Reto 7: Haz posible la siguiente división: a / 0

Reto 8: Haz la función límite ‘lim()’

#### Propuestas de

-   Sergio Durán Utrera

-   Francisco José Redondo Gonzalez

-   Manuel Arana Saborido

-   Manolo Cardeno Sanchez

-   Pablo Sanz Aznar

-   Juan Felipe Arias Aguirre

Reto propuesto:

1.  \- Calcular la división entera //
2.  \- Calcular el resto de una división %
3.  \- Añadir un histórico de operaciones
4.  \- Añadir la posibilidad de limpiar todas las operaciones

#### Propuestas de

-   Víctor Gómez Tejada
-   Pablo Fernández Fernández
-   David Benavides Foncubierta
-   José Antonio Díaz Busati

1.  Exportar resultados a txt

Con esta funcionalidad el usuario, al evaluar una expresión, dispondra del historial de la expresión junto a los resultados de esta, guardados en un archivo .txt. Dicho de otro modo se guardaran las operaciones en un archivo .txt. Para ello utilizaremos FileWriter en el propio Main.java.

2.  Convertir el Main en una sola linea de código

    Encontrar la manera de dejar el Main en una sola linea, de forma que tenga que analizar una EXPRESIÓN
