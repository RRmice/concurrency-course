package course.concurrency.m3_shared.immutable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class Order {

    public enum Status {NEW, IN_PROGRESS, DELIVERED}

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    private static final AtomicLong nextId = new AtomicLong();

    public Order(List<Item> items) {
        this(nextId.incrementAndGet(), items, null, false, Status.NEW);
    }

    public Long getId() {
        return id;
    }

    private Order(Long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = items;
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public Order withStatus(Status status) {
        return new Order(this.id, this.items, this.paymentInfo, this.isPacked, status);
    }

    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        return new Order(this.id, this.items, paymentInfo, this.isPacked, this.status);
    }

    public Order doPack() {
        return new Order(this.id, this.items, this.paymentInfo, true, this.status);
    }

    public boolean checkStatus() {
        return paymentInfo != null && isPacked;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Status getStatus() {
        return status;
    }
}
