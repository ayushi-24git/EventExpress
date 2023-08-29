import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class EventDeliveryService {
    public boolean attemptDeliveryWithRetry(Event event, int destinationID, int maxRetries, Jedis jedis) {
        int retryCount = 0;  // Current retry count
        while (retryCount <= maxRetries) {
            if (attemptDelivery(event, destinationID, jedis)) {
                return true; // Successful delivery
            }

            System.out.println("Retry " + retryCount + " for event to destination " + destinationID + ": " + event.userID);
            retryCount++;
            // Apply exponential backoff with increasing delay
            int delaySeconds = (int) Math.pow(2, retryCount);
            try {
                TimeUnit.SECONDS.sleep(delaySeconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false; // All retries exhausted
    }

    private static boolean attemptDelivery(Event event, int destinationID, Jedis jedis) {
        try {
            // Simulate successful or failed delivery logic
            int randomValue = (int) (Math.random() * 10); // Random value between 0 and 9
            if (randomValue < 8) {
                // Successful delivery with 90% probability
                jedis.rpush("events:" + destinationID, event.userID + "|" + event.payload);
                return true;
            } else {
                // Failed delivery with 10% probability
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error during delivery attempt: " + e.getMessage());
            return false;
        }
    }
}
