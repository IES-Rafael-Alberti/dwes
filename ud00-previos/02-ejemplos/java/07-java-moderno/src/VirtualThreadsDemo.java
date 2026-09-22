import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class VirtualThreadsDemo {
    private VirtualThreadsDemo() {
    }

    public static void run() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> first = executor.submit(() -> simulatedIo("primera", 150));
            Future<String> second = executor.submit(() -> simulatedIo("segunda", 100));

            System.out.println(first.get());
            System.out.println(second.get());
        }
    }

    private static String simulatedIo(String task, long milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
        return task + " tarea terminada en " + Thread.currentThread();
    }
}
