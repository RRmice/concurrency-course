package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;
    private volatile Bid latestBid;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();
    private final ExecutorService executorService = new ForkJoinPool();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid = new Bid(-1L, -1L, -1L);
    }

    public boolean propose(Bid bid) {
        if (bid.price > latestBid.price) {
            try {
                writeLock.lock();
                latestBid = bid;
            } finally {
                writeLock.unlock();
            }
            CompletableFuture.runAsync(() -> notifier.sendOutdatedMessage(latestBid), executorService);
            return true;
        }
        return false;
    }

    public Bid getLatestBid() {
        try {
            readLock.lock();
            return latestBid;
        } finally {
            readLock.unlock();
        }
    }
}
