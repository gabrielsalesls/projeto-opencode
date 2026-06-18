package dev.gabrielsales.outboxworker.publisher;

import dev.gabrielsales.outboxworker.entity.OutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OutboxEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(OutboxEvent event) {
        Message message = MessageBuilder
                .withBody(event.getPayload().getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setMessageId(event.getId().toString())
                .setType(event.getEventType())
                .build();

        rabbitTemplate.send("transfer-events", message);

        log.info("Event published to RabbitMQ: outboxEventId={}, eventType={}, queue={}",
                event.getId(), event.getEventType(), "transfer-events");
    }
}
