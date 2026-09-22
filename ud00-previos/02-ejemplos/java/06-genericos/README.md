# Genericos, comodines y PECS

Ejemplo ejecutable para separar tres ideas que suelen confundirse:

- `List<Dog>` no es una `List<Animal>`: los tipos genericos son invariantes.
- `List<? extends Animal>` permite leer cada elemento como `Animal`, sin saber
  si la lista concreta era de perros, gatos u otro subtipo.
- `List<? super Animal>` permite anadir un `Animal`, `Dog` o `Cat`; no hace
  falta envolverlos en `Object`.

El metodo `copyAnimals` muestra ambos limites juntos: la fuente produce
animales para el metodo y el destino los recibe. No necesita `instanceof`: el
limite `extends Animal` ya garantiza que todo elemento se puede usar como
`Animal`. Solo tendria sentido usar `instanceof` si hiciera falta un
comportamiento exclusivo de `Dog` o `Cat`, algo que normalmente se resuelve
mejor con polimorfismo.

```bash
javac -d out src/*.java
java -cp out Main
```

En una aplicacion DWES no se suele escribir comodines a diario al comenzar,
pero si se usan genericos constantemente: `List<Producto>`,
`Map<String, Usuario>`, `Optional<Usuario>`, `ResponseEntity<PedidoDto>` y
repositorios como `JpaRepository<Pedido, Long>`.
