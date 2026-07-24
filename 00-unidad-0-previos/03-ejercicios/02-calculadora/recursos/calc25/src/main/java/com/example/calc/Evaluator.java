package com.example.calc;

import static com.example.calc.Expr.*;

public final class Evaluator {
    private Evaluator() { throw new AssertionError("No instanciable"); }

    public static double eval(Expr e) {
        return switch (e) {
            case NumberLit n -> n.value();
            case Unary u -> {
                double v = eval(u.expr());
                yield (u.op() == '-') ? -v : +v;
            }
            case Binary b -> {
                double l = eval(b.left());
                double r = eval(b.right());
                yield switch (b.op()) {
                    case '+' -> l + r;
                    case '-' -> l - r;
                    case '*' -> l * r;
                    case '/' -> l / r;
                    case '^' -> Math.pow(l, r);
                    default -> throw new IllegalStateException("Operador no soportado: " + b.op());
                };
            }
            case Call c -> {
                double x = eval(c.arg());
                // Funciones de un argumento soportadas en Calc25
                yield switch (c.name()) {
                    case "sin"   -> Math.sin(x);
                    case "cos"   -> Math.cos(x);
                    case "tan"   -> Math.tan(x);
                    case "sqrt"  -> Math.sqrt(x);
                    case "ln"    -> Math.log(x);     // log neperiano (base e)
                    case "log10" -> Math.log10(x);   // log base 10
                    default -> throw new IllegalArgumentException("Función no soportada: " + c.name());
                };
            }
        };
    }
}
