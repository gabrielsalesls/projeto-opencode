package dev.gabrielsales.notificationapi.consumer;

import tools.jackson.databind.ObjectMapper;
import dev.gabrielsales.notificationapi.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferEventConsumerTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TransferEventConsumer consumer;

    @Captor
    private ArgumentCaptor<dev.gabrielsales.notificationapi.entity.Notification> notificationCaptor;

    @Test
    @DisplayName("consume should parse payload and save notification")
    void consume_shouldParseAndSaveNotification() throws Exception {
        String payload = """
                {"sourceAccountId":"11111111-1111-1111-1111-111111111111","destinationAccountId":"22222222-2222-2222-2222-222222222222","amount":100.0,"transferId":"33333333-3333-3333-3333-333333333333"}
                """;

        consumer.consume(payload);

        verify(notificationRepository).save(notificationCaptor.capture());
        var saved = notificationCaptor.getValue();

        assertNotNull(saved.getId());
        assertEquals(java.util.UUID.fromString("22222222-2222-2222-2222-222222222222"), saved.getAccountId());
        assertEquals(payload.strip(), saved.getMessage().strip());
        assertNotNull(saved.getCreatedAt());
    }
}
