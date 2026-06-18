package dev.gabrielsales.outboxworker.publisher;

import dev.gabrielsales.outboxworker.entity.OutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        rabbitTemplate.convertAndSend("transfer-events", event.getPayload());
        log.info("Published event {} to transfer-events queue", event.getId());
    }
}
