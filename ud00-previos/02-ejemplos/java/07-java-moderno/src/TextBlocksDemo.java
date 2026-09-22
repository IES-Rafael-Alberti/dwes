public final class TextBlocksDemo {
    private TextBlocksDemo() {
    }

    public static void run() {
        String json = """
            {
              "id": %d,
              "name": "%s",
              "active": true
            }
            """.formatted(42, "Ada");

        System.out.println(json);
    }
}
