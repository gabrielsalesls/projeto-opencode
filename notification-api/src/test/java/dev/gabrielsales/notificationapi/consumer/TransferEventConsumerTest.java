package dev.gabrielsales.notificationapi.consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferEventConsumerTest {

    private final TransferEventConsumer consumer = new TransferEventConsumer();

    @Test
    @DisplayName("consume should log the received payload without throwing")
    void consume_shouldLogPayload() {
        String payload = """
                {"eventType":"TRANSFER_CREATED","accountId":"123","amount":100.0}
                """;

        consumer.consume(payload);
    }
}
