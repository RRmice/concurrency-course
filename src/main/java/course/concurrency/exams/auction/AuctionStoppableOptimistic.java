package course.concurrency.exams.auction;

import java.util.concurrent.atomic.*;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>();
    private AtomicMarkableReference<Bid> atomicMarkableReference;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        atomicMarkableReference = new AtomicMarkableReference<>(new Bid(-1L, -1L, -1L),true);
    }

    public boolean propose(Bid bid) {
        Bid cache = null;

        do {
            boolean[] markHolder = new boolean[1];
            cache = atomicMarkableReference.get(markHolder);
            if (!(bid.price > cache.price) || !markHolder[0]) {
                return false;
            }
        } while (atomicMarkableReference.compareAndSet(cache, bid, true, true));

        notifier.sendOutdatedMessage(cache);
        return true;
    }

    public Bid getLatestBid() {
        return atomicMarkableReference.getReference();
    }

    public Bid stopAuction() {
        atomicMarkableReference.set(atomicMarkableReference.getReference(), false);
        return atomicMarkableReference.getReference();
    }
}
