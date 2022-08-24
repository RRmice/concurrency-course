package course.concurrency.m3_shared.collections;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RestaurantService {

    private ConcurrentHashMap<String, AtomicInteger> stat = new ConcurrentHashMap<>();
    private Restaurant mockRestaurant = new Restaurant("A");

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return mockRestaurant;
    }

    public void addToStat(String restaurantName) {
        stat.computeIfAbsent(restaurantName, k -> new AtomicInteger()).getAndIncrement();
    }

    public Set<String> printStat() {
        return stat.entrySet().stream().map(entry -> entry.getKey() + " - " + entry.getValue()).collect(Collectors.toSet());
    }
}
