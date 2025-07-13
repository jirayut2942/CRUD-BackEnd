# Kafka Producer Retry Implementation - POC

This document describes the implementation of a Kafka producer with retry functionality in Spring Boot.

## Overview

The implementation includes:
- **3 retry attempts** maximum
- **3 seconds wait time** between each retry attempt
- Two retry approaches: Spring Retry annotation and manual retry logic
- Comprehensive logging for monitoring retry behavior

## Configuration

### 1. Kafka Producer Configuration (`application.yml`)

```yaml
spring:
  kafka:
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      retries: 3                    # Kafka-level retries
      retry-backoff-ms: 3000        # 3 seconds between retries
      acks: all                     # Wait for all replicas to acknowledge
      enable-idempotence: true      # Prevent duplicate messages
```

### 2. Dependencies Added

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>
```

## Implementation Details

### 1. Spring Retry Approach

The `Producer.produce()` method uses Spring's `@Retryable` annotation:

```java
@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 3000))
public void produce(String data) {
    // Implementation with automatic retry handling
}
```

### 2. Manual Retry Approach

The `Producer.produceWithManualRetry()` method implements custom retry logic:

```java
public void produceWithManualRetry(String data) {
    int attempt = 0;
    boolean success = false;
    
    while (attempt < MAX_RETRY_ATTEMPTS && !success) {
        // Manual retry implementation with detailed logging
    }
}
```

## Testing the Implementation

### Prerequisites

1. Start Kafka and Zookeeper using Docker Compose:
```bash
docker-compose up -d
```

2. Start the Spring Boot application:
```bash
./mvnw spring-boot:run
```

### Test Endpoints

#### 1. Standard Kafka Message (Original)
```bash
curl "http://localhost:8080/api/v1/users/kafka?message=Hello%20World"
```

#### 2. Kafka Message with Spring Retry
```bash
curl "http://localhost:8080/api/v1/users/kafka/retry?message=Test%20Retry%20Message"
```

#### 3. Kafka Message with Manual Retry
```bash
curl "http://localhost:8080/api/v1/users/kafka/manual-retry?message=Test%20Manual%20Retry"
```

### Testing Retry Behavior

To test the retry functionality when Kafka is unavailable:

1. **Stop Kafka while the application is running:**
```bash
docker-compose stop kafka
```

2. **Send a message to trigger retry:**
```bash
curl "http://localhost:8080/api/v1/users/kafka/retry?message=This%20will%20retry"
```

3. **Check the application logs** to see retry attempts:
```
INFO: Attempting to send message to Topic: kafka - Message: This will retry
ERROR: Error sending message to Kafka - Attempt will be retried. Error: ...
INFO: Attempting to send message to Topic: kafka - Message: This will retry
ERROR: Error sending message to Kafka - Attempt will be retried. Error: ...
INFO: Attempting to send message to Topic: kafka - Message: This will retry
ERROR: All 3 retry attempts failed for message: This will retry
```

4. **Restart Kafka and test again:**
```bash
docker-compose start kafka
curl "http://localhost:8080/api/v1/users/kafka/retry?message=This%20should%20work"
```

## Key Features

### 1. Dual-Level Retry Protection
- **Kafka-level retries**: Built-in Kafka producer retries (configured in `application.yml`)
- **Application-level retries**: Custom retry logic using Spring Retry or manual implementation

### 2. Comprehensive Logging
- Each retry attempt is logged with attempt number and error details
- Success messages include Kafka offset information
- Failed attempts after all retries are clearly logged

### 3. Idempotency
- `enable-idempotence: true` prevents duplicate messages during retries
- `acks: all` ensures message durability

### 4. Flexible Retry Configuration
- Easily configurable retry count and delay
- Different retry strategies available (Spring Retry vs Manual)

## Configuration Parameters

| Parameter | Value | Description |
|-----------|-------|-------------|
| `MAX_RETRY_ATTEMPTS` | 3 | Maximum number of retry attempts |
| `RETRY_DELAY_MS` | 3000 | Delay between retries in milliseconds |
| `retries` | 3 | Kafka producer-level retries |
| `retry-backoff-ms` | 3000 | Kafka producer retry backoff |

## Monitoring and Troubleshooting

### Log Patterns to Monitor

1. **Successful sends:**
```
INFO: Message sent successfully to Topic: kafka with offset: 123
```

2. **Retry attempts:**
```
ERROR: Error sending message to Kafka - Attempt will be retried. Error: ...
INFO: Manual retry attempt 2/3 - Sending message to Topic: kafka
```

3. **Complete failures:**
```
ERROR: All 3 retry attempts failed for message: ...
```

### Common Issues

1. **Kafka not available**: Check if Kafka and Zookeeper are running
2. **Topic doesn't exist**: Ensure the 'kafka' topic exists or enable auto-creation
3. **Network issues**: Verify connectivity to `localhost:9092`

## Next Steps

1. **Add exponential backoff** for more sophisticated retry strategies
2. **Implement circuit breaker pattern** for better resilience
3. **Add metrics collection** for monitoring retry success rates
4. **Create dead letter queue** for messages that fail after all retries