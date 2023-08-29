import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class EventProcessor {
    public static void main(String[] args) {
        //Preparing queue for ingestion simulation
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Starting event ingestion from HTTP endpoint
        startEventIngestion(eventQueue);

        // Simulate delivering events
        for (int i = 0; i < 10; i++) {
            int destinationID = i;
            executor.submit(() -> deliverEvents(eventQueue, destinationID));
        }

        // Shutting down executor when done
        executor.shutdown();
    }

    private static void startEventIngestion(BlockingQueue<Event> eventQueue) {
        // Simulating ingesting events from an HTTP endpoint
        for (int i = 0; i < 100; i++) {
            Event event = new Event();
            event.userID = "user" + i;
            event.payload = "payload" + i;
            try {
                eventQueue.put(event);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void deliverEvents(BlockingQueue<Event> eventQueue, int destinationID) {
        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "redis-container", 6379);
        Jedis jedis = jedisPool.getResource();
        EventDeliveryService deliveryService = new EventDeliveryService();
        while (true) {
            try {
                Event event = eventQueue.take();
                if (deliveryService.attemptDeliveryWithRetry(event, destinationID, 5, jedis)) {
                    System.out.println("Delivered event to destination " + destinationID + ": " + event.userID);
                } else {
                    System.out.println("Failed to deliver event to destination " + destinationID + ": " + event.userID);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}

