package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>();
    private final ExecutorService executorService = new ForkJoinPool();

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid.set(new Bid(-1L, -1L, -1L));
    }

    public boolean propose(Bid bid) {
        if (bid == null) return false;
        AtomicReference<Bid> cache = new AtomicReference<>();

        do {
            cache.set(latestBid.get());
            if (!(bid.price > cache.get().price)) {
                return false;
            }
        } while (!latestBid.compareAndSet(cache.get(), bid));

        if (latestBid.get().id.equals(bid.id)){
            CompletableFuture.runAsync(() -> notifier.sendOutdatedMessage(cache.get()), executorService);
        }
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }


}
