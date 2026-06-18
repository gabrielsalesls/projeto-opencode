package dev.gabrielsales.notificationapi.consumer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import dev.gabrielsales.notificationapi.entity.Notification;
import dev.gabrielsales.notificationapi.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransferEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransferEventConsumer.class);

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    public TransferEventConsumer(NotificationRepository notificationRepository, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "transfer-events")
    public void consume(String payload) {
        log.info("Received event from transfer-events: {}", payload);

        try {
            JsonNode json = objectMapper.readTree(payload);
            UUID accountId = UUID.fromString(json.get("destinationAccountId").asText());

            var notification = new Notification();
            notification.setId(UUID.randomUUID());
            notification.setAccountId(accountId);
            notification.setMessage(payload);
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);

            log.info("Notification saved for account {}", accountId);
        } catch (Exception e) {
            log.error("Failed to process event: {}", payload, e);
        }
    }
}
