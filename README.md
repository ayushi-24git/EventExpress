# EventExpress
This assignment fulfils the following non-functional requirements:

### Durability:
Ingested events are retained in the system even if the system crashes or until they are delivered or exhaustively retried.
We are using the JedisPool to manage connections to Redis. We are indeed storing events in Redis.

For each event, we have stored the retry state ie the number of retries. This allows the system to resume retries
even after a crash or restart.

### At-least-once Delivery:
Delivery to destinations might fail for any reason. We have implemented retry strategy to ensure at-least-once delivery.

### Retry Backoff and Limit:
Messages should be retried using a backoff algorithm. After a certain number of delivery attempts, the event is drained from the system
using exponential backoff strategy.

### Maintaining Order:
Events are in a FIFO (First-In-First-Out) method for each destination by using Java's BlockingQueue.

### Delivery Isolation:
As each destination's event processing is isolated in a separate thread, issues related to delays or failures with
event delivery for one destination will not affect the processing of events for other destinations. Delays or failures
in one thread do not block or impact the processing of events in other threads, maintaining isolation between destination deliveries.

# Implementation Details:

The main class, EventProcessor, is responsible for orchestrating the event ingestion and delivery processes. It uses a
BlockingQueue to hold ingested events.

The startEventIngestion method simulates the ingestion of events from an HTTP endpoint. It generates example events and
puts them into the eventQueue.

The deliverEvents method simulates event delivery to mock destinations. It continuously takes events from the eventQueue
and attempts to deliver them to the destination using the attemptDeliveryWithRetry method.

The attemptDeliveryWithRetry method implements the retry strategy. It tries to deliver an event to a destination using a
given Jedis instance. It retries the delivery using an exponential backoff mechanism if the initial delivery attempt fails.

The attemptDelivery method simulates the actual event delivery logic. It uses a random value to determine whether the delivery succeeds or fails.

# Flow:

- Events are ingested through the startEventIngestion method. These events are added to the eventQueue.

- The deliverEvents method runs concurrently in multiple threads, simulating the delivery process. Each thread takes
events from the eventQueue and tries to deliver them using the attemptDeliveryWithRetry method.

- The attemptDeliveryWithRetry method attempts to deliver the event using the attemptDelivery method. If the initial
delivery fails, it implements a retry strategy with an exponential backoff. The goal is to ensure successful delivery
while mitigating the effects of potential failures.

- The attemptDelivery method simulates the delivery logic by randomly determining whether the delivery succeeds or fails.
This emulates the scenario where delivery to actual destinations might fail due to various reasons.

- If an event is successfully delivered, a message indicating success is printed. If all retry attempts are exhausted and
delivery is still unsuccessful, a message indicating failure is printed.

- The use of Jedis and the jedisPool helps manage connections to the Redis server efficiently, and the implementation
ensures that connection resources are properly managed.

- The system operates concurrently, with multiple threads handling event delivery, mimicking a real-world scenario where
multiple events are being processed and delivered simultaneously.

In summary, the system demonstrates a basic implementation of event ingestion and delivery, with emphasis on
retry strategies, connection management, and simulation of potential delivery failures.
