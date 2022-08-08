package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>();

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
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

    public boolean updateAndSend(Bid expected, Bid newValue) {
        if (latestBid.compareAndSet(expected, newValue)) {
            CompletableFuture.runAsync(() -> notifier.sendOutdatedMessage(expected));
            return false;
        }
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }


}
