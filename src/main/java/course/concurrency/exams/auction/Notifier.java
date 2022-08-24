package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class Notifier {

    private final ExecutorService executorService = new ForkJoinPool();

    public void sendOutdatedMessage(Bid bid) {
        CompletableFuture.runAsync(this::imitateSending, executorService);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
