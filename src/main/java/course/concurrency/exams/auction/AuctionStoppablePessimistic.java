package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;
    private volatile Bid latestBid;
    private volatile boolean stopped;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.stopped = false;
    }

    public synchronized boolean propose(Bid bid) {
        if (stopped){
            return false;
        }

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

    public synchronized Bid stopAuction() {
        stopped = true;
        return latestBid;
    }
}
