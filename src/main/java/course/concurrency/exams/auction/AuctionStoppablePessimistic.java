package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;
    private volatile Bid latestBid;
    private volatile boolean inProgress;
    private final Object monitor = new Object();

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.inProgress = true;
        latestBid = new Bid(-1L, -1L, -1L);
    }

    public synchronized boolean propose(Bid bid) {
        if (bid.price > latestBid.price) {
            synchronized (monitor) {
                if (bid.price > latestBid.price && inProgress) {
                    latestBid = bid;
                    notifier.sendOutdatedMessage(latestBid);
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized Bid getLatestBid() {
        return latestBid;
    }

    public synchronized Bid stopAuction() {
        synchronized (monitor){
            inProgress = false;
        }
        return latestBid;
    }
}
