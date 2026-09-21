public sealed interface Outcome permits Success, Failure {}

record Success(String resource) implements Outcome {}

record Failure(String code, String message) implements Outcome {}
