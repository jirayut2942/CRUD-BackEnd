package com.example.backend;

import com.example.backend.Service.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test class to demonstrate and validate Kafka Producer retry functionality
 * This class tests both Spring Retry and Manual Retry approaches
 */
@ExtendWith(MockitoExtension.class)
class ProducerRetryTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private Producer producer;

    private SettableListenableFuture<SendResult<String, String>> future;

    @BeforeEach
    void setUp() {
        future = new SettableListenableFuture<>();
    }

    /**
     * Test successful message sending without retries
     */
    @Test
    void testSuccessfulMessageSending() throws Exception {
        // Arrange
        String testMessage = "Test message";
        SendResult<String, String> mockResult = mock(SendResult.class);
        future.set(mockResult);
        
        when(kafkaTemplate.send(eq("kafka"), eq(testMessage))).thenReturn(future);

        // Act
        assertDoesNotThrow(() -> producer.produce(testMessage));

        // Assert
        verify(kafkaTemplate, times(1)).send("kafka", testMessage);
    }

    /**
     * Test retry behavior with execution exception
     */
    @Test
    void testRetryOnExecutionException() throws Exception {
        // Arrange
        String testMessage = "Test retry message";
        future.setException(new RuntimeException("Connection failed"));
        
        when(kafkaTemplate.send(eq("kafka"), eq(testMessage))).thenReturn(future);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> producer.produce(testMessage));
        
        // Note: With Spring Retry, the actual retry count depends on the Spring Retry configuration
        // This test demonstrates that exceptions are properly thrown for retry handling
    }

    /**
     * Test manual retry functionality with multiple failures
     */
    @Test
    void testManualRetryWithMultipleFailures() throws Exception {
        // Arrange
        String testMessage = "Test manual retry";
        SettableListenableFuture<SendResult<String, String>> failingFuture1 = new SettableListenableFuture<>();
        SettableListenableFuture<SendResult<String, String>> failingFuture2 = new SettableListenableFuture<>();
        SettableListenableFuture<SendResult<String, String>> successFuture = new SettableListenableFuture<>();
        
        failingFuture1.setException(new RuntimeException("First attempt failed"));
        failingFuture2.setException(new RuntimeException("Second attempt failed"));
        SendResult<String, String> mockResult = mock(SendResult.class);
        successFuture.set(mockResult);
        
        when(kafkaTemplate.send(eq("kafka"), eq(testMessage)))
            .thenReturn(failingFuture1)
            .thenReturn(failingFuture2)
            .thenReturn(successFuture);

        // Act
        assertDoesNotThrow(() -> producer.produceWithManualRetry(testMessage));

        // Assert
        verify(kafkaTemplate, times(3)).send("kafka", testMessage);
    }

    /**
     * Test manual retry exhaustion (all attempts fail)
     */
    @Test
    void testManualRetryExhaustion() throws Exception {
        // Arrange
        String testMessage = "Test retry exhaustion";
        SettableListenableFuture<SendResult<String, String>> failingFuture = new SettableListenableFuture<>();
        failingFuture.setException(new RuntimeException("All attempts failed"));
        
        when(kafkaTemplate.send(eq("kafka"), eq(testMessage))).thenReturn(failingFuture);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> producer.produceWithManualRetry(testMessage));
        
        assertTrue(exception.getMessage().contains("Failed to send message after 3 attempts"));
        verify(kafkaTemplate, times(3)).send("kafka", testMessage);
    }

    /**
     * Integration test to demonstrate retry timing
     * This test shows the 3-second delay between retries
     */
    @Test
    void testRetryTimingDemonstration() throws Exception {
        // Arrange
        String testMessage = "Test timing";
        SettableListenableFuture<SendResult<String, String>> failingFuture1 = new SettableListenableFuture<>();
        SettableListenableFuture<SendResult<String, String>> failingFuture2 = new SettableListenableFuture<>();
        SettableListenableFuture<SendResult<String, String>> successFuture = new SettableListenableFuture<>();
        
        failingFuture1.setException(new RuntimeException("First attempt failed"));
        failingFuture2.setException(new RuntimeException("Second attempt failed"));
        SendResult<String, String> mockResult = mock(SendResult.class);
        successFuture.set(mockResult);
        
        when(kafkaTemplate.send(eq("kafka"), eq(testMessage)))
            .thenReturn(failingFuture1)
            .thenReturn(failingFuture2)
            .thenReturn(successFuture);

        // Act
        long startTime = System.currentTimeMillis();
        producer.produceWithManualRetry(testMessage);
        long endTime = System.currentTimeMillis();

        // Assert
        // The test should take at least 6 seconds (2 failures × 3 seconds delay)
        long executionTime = endTime - startTime;
        assertTrue(executionTime >= 6000, 
            "Expected execution time to be at least 6 seconds, but was " + executionTime + "ms");
        
        verify(kafkaTemplate, times(3)).send("kafka", testMessage);
    }
}