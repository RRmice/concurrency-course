package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;
    private volatile Bid latestBid;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    public synchronized boolean propose(Bid bid) {
        boolean result = false;
        if (latestBid == null || bid.price > latestBid.price) {
            CompletableFuture.runAsync(() -> notifier.sendOutdatedMessage(latestBid));
            latestBid = bid;
            result = true;
        }
        return result;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        // ваш код
        return latestBid;
    }
}
