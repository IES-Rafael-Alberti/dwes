# Calc25 — Calculadora educativa (Java 25)

- Expresiones: `+ - * / ^`, paréntesis, funciones `sin(x)`, `cos(x)`.
- Arquitectura por capas: Lexer → Parser → AST → Evaluator → REPL.
- Java moderno: records + sealed, switch con pattern matching, text blocks.

## Recorrido de trabajo

1. Ejecuta la aplicación y prueba expresiones válidas y errores de sintaxis.
2. Lee el flujo `Lexer -> Parser -> AST -> Evaluator` y localiza dónde se
   transforma cada entrada.
3. Añade el comando `history` siguiendo `J25-07`: conserva las últimas
   operaciones y resultados mediante un `record`, sin modificar lexer ni
   parser.
4. Escribe tests para el límite del historial y el orden más reciente.
5. Explica una decisión de diseño y una diferencia entre el starter y tu
   solución.

La ampliación es formativa y no tiene nota independiente. No guardes todavía
el historial en un fichero: esa variante se reserva para una práctica posterior
cuando se haya trabajado NIO.2.

- Ejecutar:
  ```bash
  ./gradlew run
  ```
- Tests:
  ```bash
  ./gradlew test
  ```

> Si falta el wrapper JAR: `gradle wrapper --gradle-version 9.1.0` (una vez).
