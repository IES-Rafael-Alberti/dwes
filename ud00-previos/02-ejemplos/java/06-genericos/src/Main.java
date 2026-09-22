import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Dog> dogs = List.of(new Dog("Rex"), new Dog("Luna"));
        List<Cat> cats = List.of(new Cat("Milo"));

        System.out.println("Leer desde listas de subtipos:");
        printAnimals(dogs);
        printAnimals(cats);

        List<Animal> shelter = new ArrayList<>();
        addAnimals(shelter);
        System.out.println("\nAnimal se puede anadir a List<Animal>:");
        printAnimals(shelter);

        List<Object> audit = new ArrayList<>();
        addAnimals(audit);
        System.out.println("\nTambien se puede anadir a List<Object>: " + audit.size() + " animales");

        List<Animal> copied = new ArrayList<>();
        copyAnimals(dogs, copied);
        System.out.println("\nCopiar de List<Dog> a List<Animal>:");
        printAnimals(copied);

        // List<Animal> animals = dogs; // No compila: List<Dog> no es List<Animal>.
    }

    // La lista produce Animal para este metodo: solo se lee de ella.
    static void printAnimals(List<? extends Animal> source) {
        for (Animal animal : source) {
            System.out.println(animal.name() + " hace " + animal.makeNoise());
        }
    }

    // La lista recibe Animal de este metodo: se pueden anadir Animal y subtipos.
    static void addAnimals(List<? super Animal> target) {
        target.add(new Dog("Toby"));
        target.add(new Cat("Nala"));
    }

    static void copyAnimals(List<? extends Animal> source, List<? super Animal> target) {
        for (Animal animal : source) {
            target.add(animal);
        }
    }
}
