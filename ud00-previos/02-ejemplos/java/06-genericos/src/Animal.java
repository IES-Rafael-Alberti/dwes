public abstract class Animal {
    private final String name;

    protected Animal(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public abstract String makeNoise();
}
