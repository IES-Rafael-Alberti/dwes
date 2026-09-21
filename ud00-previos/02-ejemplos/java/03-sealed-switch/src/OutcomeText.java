public final class OutcomeText {
    private OutcomeText() {}

    public static String describe(Outcome outcome) {
        return switch (outcome) {
            case Success success -> "Created: " + success.resource();
            case Failure failure when failure.code().equals("DB-001") ->
                "Urgent failure: " + failure.message();
            case Failure failure -> "Failure: " + failure.message();
        };
    }
}
