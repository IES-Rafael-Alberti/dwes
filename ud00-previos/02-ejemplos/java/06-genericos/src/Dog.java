public class Dog extends Animal {
    public Dog(String name) {
        super(name);
    }

    @Override
    public String makeNoise() {
        return "guau";
    }
}
