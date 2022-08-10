package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>();
    private final AtomicBoolean inProgress = new AtomicBoolean(true);

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid.set(new Bid(-1L, -1L, -1L));
    }

    public boolean propose(Bid bid) {
        Bid cache = null;

        do {
            cache = latestBid.get();
            if (!(bid.price > cache.price)) {
                return false;
            }
        } while (inProgress.get() && !latestBid.compareAndSet(cache, bid));

        notifier.sendOutdatedMessage(cache);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }

    public Bid stopAuction() {
        inProgress.compareAndSet(true, false);
        return latestBid.get();
    }
}
