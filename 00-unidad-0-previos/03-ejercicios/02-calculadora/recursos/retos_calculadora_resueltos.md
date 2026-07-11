---

## 📖 Retos de comprensión

**1) ¿Qué es un Token? Ejemplo con `3 + 5 * 2`.**
Un **token** es una unidad con significado léxico (tipo + lexema + posición).
Para `3 + 5 * 2` el lexer generaría algo así:

* `NUMBER("3")`, `PLUS("+")`, `NUMBER("5")`, `STAR("*")`, `NUMBER("2")`.
  Sirve para que el parser no tenga que leer caracteres sueltos.

**2) Diferencia entre Lexer y Parser.**

* **Lexer**: convierte texto → lista de **tokens**. Se ocupa de separar números, identificadores, símbolos, etc.
* **Parser**: convierte tokens → **AST** (árbol de sintaxis). Aplica **gramática** y **precedencia** de operadores.

**3) ¿Qué significa que el parser sea “recursivo”?**
Usa **funciones que se llaman entre sí** (o a sí mismas) siguiendo las reglas de la gramática.
Ejemplo típico:

* `expr()` llama a `term()`;
* `term()` llama a `factor()`;
* `factor()` puede llamar a `primary()`;
* `primary()` puede volver a llamar a `expr()` si ve paréntesis.
  Esto modela de forma natural la estructura jerárquica de las expresiones.

---

## 🔍 Retos de depuración

**4) Resultado de `2 + 3 * 4` y por qué.**
`*` tiene mayor precedencia que `+`. Primero `3*4=12`; luego `2+12=14`.
**Respuesta:** `14`.

**5) ¿Qué pasa con `2 + *`?**
Es **inválida**. El parser espera un `term` tras `+` y se encuentra `*`. Debe lanzar un **error sintáctico** indicando token inesperado/fin inesperado.

**6) ¿Qué devuelve `(2 + 3) ^ 2`?**
Primero paréntesis: `2+3=5`. Luego potencia: `5^2 = 25`.
**Respuesta:** `25`.

---

## 🛠️ Retos de modificación

**7) Añadir `tan(x)`.**
En el **evaluador** (p. ej. `Evaluator.eval` en el `case Call c`):

```java
case "tan" -> Math.tan(x);
```

(No olvides añadir test: `assertEquals(0.0, run("tan(0)"), 1e-9);`)

**8) Aceptar `sqrt(x)`.**
Similar a `tan`, en `Evaluator`:

```java
case "sqrt" -> Math.sqrt(x);
```

Si el parser ya reconoce `IDENT '(' expr ')'` no hay que tocarlo; basta con soportar el nombre en el evaluador.

**9) Números negativos explícitos `-5 + 3`.**
Debe existir una regla de **unario**:

```
unary -> ('+'|'-') unary | primary
```

En el AST un nodo `Unary('-', expr)`. En el evaluador, si `op == '-'`, devolver `-eval(expr)`.
Con eso, `-5 + 3` → `(-5) + 3 = -2`.

---

## 🧠 Retos de predicción

**10) `cos(0) + sin(90)` (radianes).**

* `cos(0) = 1`.
* `sin(90)` si **90 se interpreta en radianes** → `sin(90 rad)` ≈ `0.8939966636`.
  **Total ≈ 1.8939966636**.

> Nota didáctica: muchos alumnos esperan grados. Aclara que Java usa **radianes**. Si quisieras grados: `sin(Math.toRadians(90)) = sin(π/2) = 1`.

**11) `2 ^ 3 ^ 2` con potencia **asociativa a derecha**.**
Se interpreta como `2 ^ (3 ^ 2)` → `2 ^ 9 = 512`.
**Respuesta:** `512`.
(Si fuera a izquierda sería `(2^3)^2=8^2=64`, pero aquí **no**).

**12) `(2 + 3) * (4 + 5)`**
Paréntesis primero: `(2+3)=5`, `(4+5)=9`, luego `5*9=45`.
**Respuesta:** `45`.

---

## 🎲 Retos de diseño

**13) Ventajas del parser recursivo vs. bucle/pila manual.**

* El código **sigue la gramática** de forma casi literal → más legible y mantenible.
* Manejar **precedencia** y **asociatividad** es más sencillo con funciones por nivel (expr/term/factor…).
* Para un lenguaje pequeño/mediano es **rápido de implementar** y fácil de enseñar.

**14) Separar en fases (lexer → parser → evaluator) vs. “todo en uno”.**

* **Separación de responsabilidades**: cada fase hace una cosa y la hace bien.
* **Testabilidad**: puedes testear lexer y parser por separado.
* **Extensibilidad**: añadir funciones/operadores afecta menos al resto.
* **Mensajes de error** más claros: posición léxica y token esperado real.

**15) Soporte para variables (`x = 5`, `y = 2 * x`): ¿dónde y cómo?**

* **AST**: añadir nodos `Var(name)`, `Assign(name, Expr)`; quizá `Env` (mapa) en evaluador.
* **Parser**: regla para asignación de forma preferible con menor precedencia que `expr`, por ejemplo:

  ```
  stmt -> IDENT '=' expr | expr
  ```

  (o integrar en `expr` si lo queréis compacto).
* **Evaluator**: mantener un **entorno** (p. ej. `Map<String,Double> env`).

  * `Assign(name, expr)`: evalúa `expr`, guarda en `env`, devuelve valor.
  * `Var(name)`: devuelve `env.get(name)` (con error si no existe).

---

### Mini-snippets útiles

**Unario en evaluador**

```java
case Unary u -> {
    double v = eval(u.expr());
    yield (u.op() == '-') ? -v : +v;
}
```

**Llamadas a funciones (ampliable)**

```java
case Call c -> {
    double x = eval(c.arg());
    yield switch (c.name()) {
        case "sin" -> Math.sin(x);
        case "cos" -> Math.cos(x);
        case "tan" -> Math.tan(x);
        case "sqrt" -> Math.sqrt(x);
        case "ln" -> Math.log(x);
        case "log10" -> Math.log10(x);
        default -> throw new IllegalArgumentException("Función no soportada: " + c.name());
    };
}
```

**Asignación y variables (orientativo)**

```java
// Evaluator state (si añadís variables)
private final Map<String, Double> env = new HashMap<>();

// En eval:
case Assign a -> {
    double v = eval(a.value());
    env.put(a.name(), v);
    yield v;
}
case Var v -> {
    Double val = env.get(v.name());
    if (val == null) throw new IllegalArgumentException("Variable no definida: " + v.name());
    yield val;
}
```

---

