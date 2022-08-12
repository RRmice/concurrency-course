package course.concurrency.exams.auction;

import java.util.concurrent.atomic.*;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicMarkableReference<Bid> latestBid;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid = new AtomicMarkableReference<>(new Bid(-1L, -1L, -1L),true);
    }

    public boolean propose(Bid bid) {
        Bid cache = null;

        do {
            cache = latestBid.getReference();
            if (!(bid.price > cache.price) || !latestBid.isMarked()) {
                return false;
            }
        } while (!latestBid.compareAndSet(cache, bid, true, true));

        notifier.sendOutdatedMessage(cache);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        while (!latestBid.attemptMark(latestBid.getReference(), false)){
        }
        return latestBid.getReference();
    }
}
