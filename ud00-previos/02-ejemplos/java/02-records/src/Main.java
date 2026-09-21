public class Main {
    public static void main(String[] args) {
        var first = new Student("Ada", 30);
        var second = new Student("Ada", 30);

        System.out.println(first.name());
        System.out.println(first);
        System.out.println(first.equals(second));
    }
}
