public sealed interface Result permits Success, Failure {
}

record Success(String value) implements Result {
}

record Failure(String message) implements Result {
}
