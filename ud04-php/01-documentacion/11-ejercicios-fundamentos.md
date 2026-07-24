# Banco de 20 ejercicios de fundamentos

Este banco conserva, uno a uno, los diez ejercicios básicos y los diez ejercicios de funciones históricos. Publica contratos y casos límite, **no soluciones completas**. Las repeticiones de factorial y primos son intencionales: primero se resuelven como script y después se refactorizan a funciones comprobables.

## Bloque A — Scripts básicos

### A1. Primer script PHP

Mostrar un mensaje, la versión principal de PHP y una expresión aritmética. Distinguir texto literal, interpolación y `PHP_EOL`.

### A2. Manipulación de variables

Declarar valores de tipos distintos, mostrar valor y tipo e intercambiar dos variables. Comparar `==` y `===` con un caso que produzca resultados diferentes.

### A3. Saludo condicional

Recibir una hora entre 0 y 23 y seleccionar mañana, tarde o noche. Cubrir exactamente los límites de cada franja.

### A4. Clasificador por edad

Clasificar una edad como menor, adulta o sénior. Rechazar negativos y justificar los límites.

### A5. Recorrido de números

Recorrer un rango con `for`, `while` y `do-while`. Incluir un caso razonado de `continue` y otro de `break`.

### A6. Iteración de array

Recorrer una lista y un catálogo asociativo/multidimensional. Obtener nombres, buscar de forma estricta y ordenar sin perder claves cuando sean significativas.

### A7. Calculadora de factorial

Calcular el factorial dentro de un pequeño script. Cubrir 0, 1, positivos y rechazo de negativos.

### A8. Detector de números primos

Decidir si un entero es primo. Cubrir negativos, 0, 1 y 2 y evitar divisiones posteriores a la raíz cuadrada.

### A9. Intentos con `do-while`

Modelar hasta tres intentos de nombre de usuario. El ejercicio recibe una colección de intentos para que su comportamiento sea reproducible; no lee consola dentro de la regla.

### A10. Selector de colores

Traducir una clave a un nombre de color mediante `match` o `switch`, incluyendo un caso desconocido. Explicar la diferencia de comparación entre ambas estructuras.

## Bloque B — Refactorización mediante funciones

### B1. Función de saludo

Crear una función que reciba un nombre y devuelva un saludo. Rechazar una cadena vacía después de `trim`.

### B2. Función suma

Recibir dos números y devolver su suma. Probar enteros, decimales y negativos sin variables globales.

### B3. Conversión de temperatura

Convertir Celsius a Fahrenheit y viceversa. Separar fórmula, unidad y política de redondeo.

### B4. Inversión de cadena

Invertir una cadena. Documentar el límite de una solución byte a byte frente a texto multibyte.

### B5. Calculadora de áreas

Calcular áreas de círculo, rectángulo y triángulo. Rechazar dimensiones negativas y separar la selección de figura del cálculo.

### B6. Función factorial

Extraer A7 a `factorial(int $n): int`. Comparar versión iterativa y recursiva con caso base explícito.

### B7. Función de primalidad

Extraer A8 a `isPrime(int $n): bool`. Añadir una tabla de casos y evitar efectos secundarios.

### B8. Wrapper HTML seguro

Crear una función que envuelva **texto ya escapado** en una etiqueta elegida desde una lista cerrada. No aceptar nombres arbitrarios procedentes del cliente.

### B9. Filtro de array

Filtrar números mediante un predicado recibido como closure. Comprobar si se conservan claves y decidir explícitamente si reindexar.

### B10. Tabla de multiplicar

Devolver los productos de un número del 1 al límite indicado. La función devuelve datos; una plantilla separada produce HTML. Como ampliación, convertirla en generador perezoso.

## Progresión y dificultad

- **Inicio**: A1–A4 y B1–B3.
- **Intermedio**: A5–A10 y B4–B7.
- **Consolidación**: B8–B10, closures, plantillas y generadores.

Para cada ejercicio se entrega contrato de entrada/salida/errores, primer caso RED, implementación mínima GREEN, un límite, un caso inválido y refactorización. El harness y comando oficial llegarán con el starter de GTask; este banco no afirma que exista todavía una suite común ejecutable.
