package dev.gabrielsales.outboxworker.publisher;

import dev.gabrielsales.outboxworker.entity.OutboxEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OutboxEventPublisher outboxEventPublisher;

    @Test
    @DisplayName("publish should send message with messageId equal to event id")
    void publish_shouldSendMessageWithMessageIdEqualToEventId() {
        var eventId = UUID.randomUUID();
        var payload = "{\"transferId\":\"abc\"}";

        var event = new OutboxEvent();
        event.setId(eventId);
        event.setEventType("TRANSFER_CREATED");
        event.setPayload(payload);
        event.setProcessed(false);
        event.setCreatedAt(LocalDateTime.now());

        outboxEventPublisher.publish(event);

        var messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).send(eq("transfer-events"), messageCaptor.capture());

        Message sentMessage = messageCaptor.getValue();
        MessageProperties props = sentMessage.getMessageProperties();

        assertEquals(eventId.toString(), props.getMessageId());
        assertEquals("TRANSFER_CREATED", props.getType());
        assertEquals(MessageProperties.CONTENT_TYPE_JSON, props.getContentType());
        assertEquals(payload, new String(sentMessage.getBody(), StandardCharsets.UTF_8));
    }

    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}
