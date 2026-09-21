public record Student(String name, int age) {
    public Student {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        if (age < 0) {
            throw new IllegalArgumentException("age cannot be negative");
        }
    }
}
