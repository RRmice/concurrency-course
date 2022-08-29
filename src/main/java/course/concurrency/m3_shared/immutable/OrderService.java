package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class OrderService {

    private final ConcurrentHashMap<Long, AtomicReference<Order>> currentOrders = new ConcurrentHashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), new AtomicReference<>(order));
        return order.getId();
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order current, recent;
        do {
            current = currentOrders.get(orderId).get();
            recent = current.withPaymentInfo(paymentInfo);
        } while (!currentOrders.get(orderId).compareAndSet(current, recent));
        deliver(recent);
    }

    public void setPacked(long orderId) {
        Order current, recent;
        do {
            current = currentOrders.get(orderId).get();
            recent = current.doPack();
        } while (!currentOrders.get(orderId).compareAndSet(current, recent));
        deliver(recent);
    }

    private void deliver(Order order) {
        if (order.checkStatus()) {
           return;
        }

        Order current, recent;
        do {
            current = currentOrders.get(order.getId()).get();
            recent = current.withStatus(Order.Status.DELIVERED);
        } while (!currentOrders.get(order.getId()).compareAndSet(current, recent));
    }

    public boolean isDelivered ( long orderId){
        return currentOrders.get(orderId).get().getStatus().equals(Order.Status.DELIVERED);
    }
}
