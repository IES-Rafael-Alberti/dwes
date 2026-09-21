public class Main {
    public static void main(String[] args) {
        System.out.println(OutcomeText.describe(new Success("note-1")));
        System.out.println(OutcomeText.describe(new Failure("DB-001", "Database unavailable")));
    }
}
