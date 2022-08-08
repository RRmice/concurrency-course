package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>();
    private volatile boolean stopped;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.stopped = false;
    }

    public boolean propose(Bid bid) {
        if (bid == null) return false;
        Bid cache = null;

        do {
            cache = latestBid.get();
            if (!(cache == null || bid.price > cache.price)) {
                return false;
            }
        } while (updateAndSend(cache, bid));

        return true;
    }

    private boolean updateAndSend(Bid expected, Bid newValue) {
        if (stopped) {
            return true;
        }
        if (latestBid.compareAndSet(expected, newValue)) {
            CompletableFuture.runAsync(() -> notifier.sendOutdatedMessage(expected));
            return false;
        }
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }

    public Bid stopAuction() {
        stopped = true;
        return latestBid.get();
    }
}
