package dev.gabrielsales.notificationapi.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransferEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferEventConsumer.class);

    @RabbitListener(queues = "transfer-events")
    public void consume(String payload) {
        log.info("Received event from transfer-events: {}", payload);
    }
}
