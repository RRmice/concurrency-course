package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;

public class AuctionPessimistic implements Auction {

    private volatile Notifier notifier;
//    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//    Lock writeLock = lock.writeLock();
//    Lock readLock = lock.readLock();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    public synchronized boolean propose(Bid bid) {
        boolean result = false;
        if (latestBid == null || bid.price > latestBid.price) {
            CompletableFuture.runAsync(() -> notifier.sendOutdatedMessage(latestBid));
            latestBid = bid;
            result = true;
        }
        return result;
    }

    public synchronized Bid getLatestBid() {
        return latestBid;
    }
}
