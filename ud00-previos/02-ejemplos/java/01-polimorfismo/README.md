# Polimorfismo

Ejemplo mínimo para observar una clase abstracta y dos implementaciones
concretas. `Main` trabaja con referencias de tipo `Figura`, pero cada objeto
calcula su área con su propia implementación.

Compilar y ejecutar desde esta carpeta:

```bash
javac -d out src/figuras/*.java src/Main.java
java -cp out Main
```

El ejemplo es deliberadamente pequeño. No es un proyecto Gradle ni una tarea
evaluable; sirve como apoyo de clase para explicar herencia, `abstract` y
`@Override`.
