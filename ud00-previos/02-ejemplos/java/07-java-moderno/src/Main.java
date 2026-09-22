public class Main {
    public static void main(String[] args) throws Exception {
        run("var y switch", VarAndSwitchDemo::run);
        run("text blocks", TextBlocksDemo::run);
        run("records, sealed y patrones", ResultDemo::run);
        run("virtual threads", VirtualThreadsDemo::run);
        run("colecciones secuenciadas", SequencedCollectionsDemo::run);
    }

    private static void run(String title, Demo demo) throws Exception {
        System.out.println("\n=== " + title + " ===");
        demo.run();
    }

    @FunctionalInterface
    interface Demo {
        void run() throws Exception;
    }
}
