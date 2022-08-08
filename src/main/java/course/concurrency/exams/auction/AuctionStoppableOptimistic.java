package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;
    private final AtomicReference<Bid> latestBid = new AtomicReference<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock writeLock = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();
    private boolean stopped;


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
       try {
           readLock.lock();
           if (stopped) {
               return true;
           }
       } finally {
           readLock.unlock();
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
        try {
            writeLock.lock();
            stopped = true;
        } finally {
            writeLock.unlock();
        }
        return latestBid.get();
    }
}
