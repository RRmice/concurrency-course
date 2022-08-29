package course.concurrency.m3_shared.immutable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {

    private final Map<Long, Order> currentOrders = new HashMap<>();

    public long createOrder(List<Item> items) {
        Order order = new Order(items);
        currentOrders.put(order.getId(), order);
        return order.getId();
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order current = currentOrders.get(orderId).withPaymentInfo(paymentInfo);
        currentOrders.put(orderId, current);
        deliver(current);
    }

    public void setPacked(long orderId) {
        Order current = currentOrders.get(orderId).doPack();
        currentOrders.put(orderId, current);
        deliver(current);
    }

    private void deliver(Order order) {
        if (order.checkStatus()) {
           return;
        }

        Order  current = currentOrders.get(order.getId()).withStatus(Order.Status.DELIVERED);
        currentOrders.put(order.getId(), current);
    }

    public boolean isDelivered ( long orderId){
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
