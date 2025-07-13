package com.example.backend.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableRetry
public class Producer {

	private static final Logger logger = LogManager.getFormatterLogger(Producer.class);
	private static String TOPIC = "kafka";
	private static final int MAX_RETRY_ATTEMPTS = 3;
	private static final long RETRY_DELAY_MS = 3000;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Retryable(value = {Exception.class}, maxAttempts = MAX_RETRY_ATTEMPTS, backoff = @Backoff(delay = RETRY_DELAY_MS))
	public void produce(String data) {
		try {
			logger.info("Attempting to send message to Topic: %s - Message: %s", TOPIC, data);
			
			// Send message with callback to handle success/failure
			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, data);
			
			future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
				@Override
				public void onSuccess(SendResult<String, String> result) {
					logger.info("Message sent successfully to Topic: %s with offset: %d", 
						TOPIC, result.getRecordMetadata().offset());
				}

				@Override
				public void onFailure(Throwable ex) {
					logger.error("Failed to send message to Topic: %s - Error: %s", TOPIC, ex.getMessage());
					throw new RuntimeException("Kafka send failed", ex);
				}
			});
			
			// Wait for the result to ensure we can catch exceptions for retry
			future.get();
			
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error sending message to Kafka - Attempt will be retried. Error: %s", e.getMessage());
			throw new RuntimeException("Kafka send operation failed", e);
		}
	}
	
	// Alternative method with manual retry logic for more control
	public void produceWithManualRetry(String data) {
		int attempt = 0;
		boolean success = false;
		
		while (attempt < MAX_RETRY_ATTEMPTS && !success) {
			attempt++;
			try {
				logger.info("Manual retry attempt %d/%d - Sending message to Topic: %s - Message: %s", 
					attempt, MAX_RETRY_ATTEMPTS, TOPIC, data);
				
				ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, data);
				SendResult<String, String> result = future.get();
				
				logger.info("Message sent successfully on attempt %d to Topic: %s with offset: %d", 
					attempt, TOPIC, result.getRecordMetadata().offset());
				success = true;
				
			} catch (Exception e) {
				logger.error("Attempt %d/%d failed to send message to Topic: %s - Error: %s", 
					attempt, MAX_RETRY_ATTEMPTS, TOPIC, e.getMessage());
				
				if (attempt < MAX_RETRY_ATTEMPTS) {
					logger.info("Waiting %d seconds before retry attempt %d", RETRY_DELAY_MS / 1000, attempt + 1);
					try {
						TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						throw new RuntimeException("Retry interrupted", ie);
					}
				} else {
					logger.error("All %d retry attempts failed for message: %s", MAX_RETRY_ATTEMPTS, data);
					throw new RuntimeException("Failed to send message after " + MAX_RETRY_ATTEMPTS + " attempts", e);
				}
			}
		}
	}
}
