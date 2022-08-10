package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>();

    public AuctionOptimistic(Notifier notifier) {
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
        } while (!latestBid.compareAndSet(cache, bid));

        notifier.sendOutdatedMessage(cache);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }


}
