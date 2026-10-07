public record Student(String name, int age) {
    public Student {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (age < 0) {
            throw new IllegalArgumentException("age cannot be negative");
        }
    }

    // El record genera toString(), equals() y hashCode() automáticamente.
    // En clase podemos sobrescribirlos si el dominio necesita otra representación
    // o una regla de igualdad distinta. Si cambia equals(), hashCode() debe
    // seguir considerando iguales los mismos objetos; no basta con cambiar uno.
}
