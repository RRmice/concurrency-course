package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class AuctionPessimistic implements Auction {

    private final Notifier notifier;
    private volatile Bid latestBid;
    private final Object monitor = new Object();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
        latestBid = new Bid(-1L, -1L, -1L);
    }

    public boolean propose(Bid bid) {
        if (bid.price > latestBid.price) {
            synchronized (monitor) {
                if (bid.price > latestBid.price) {
                    latestBid = bid;
                    notifier.sendOutdatedMessage(latestBid);
                    return true;
                }
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }
}
