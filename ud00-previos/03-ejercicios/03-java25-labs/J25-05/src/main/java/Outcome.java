public sealed interface Outcome permits Success, Failure {
}

record Success(String resourceName) implements Outcome {
}

record Failure(String code, String message) implements Outcome {
}
