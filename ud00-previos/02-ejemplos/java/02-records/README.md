# Records

Ejemplo mínimo de `record`, constructor compacto, validación y accesores sin
`getters`. Antes de hacer cambios, ejecutar `Main` y observar el `toString()`
y el `equals()` que Java genera por defecto.

Para ampliar el ejemplo en directo, se pueden sobrescribir esos métodos cuando
hay una necesidad concreta:

- `toString()`: mostrar un texto legible para una pantalla o informe en vez de
  `Student[name=Ada, age=30]`.
- `equals()` y `hashCode()`: tratar como iguales nombres que el dominio haya
  decidido normalizar, o comparar por un identificador estable en vez de por
  todos los componentes. Hay que mantener **ambos** métodos coherentes,
  especialmente si los objetos se usan en `HashSet` o como claves de un mapa.

Son cambios de comportamiento, no requisitos de cualquier `record`. Como
experimento, comparar `Student("Ada", 30)` con `Student("ada", 30)` antes y
después de implementar una regla de igualdad propia. Cuidado con suponer que
`equalsIgnoreCase()` y `toLowerCase(Locale.ROOT)` implementan exactamente la
misma regla para todos los caracteres Unicode.

```bash
javac -d out src/*.java
java -cp out Main
```
