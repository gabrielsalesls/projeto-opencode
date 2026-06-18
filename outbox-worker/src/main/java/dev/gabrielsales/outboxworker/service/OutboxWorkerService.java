package dev.gabrielsales.outboxworker.service;

import dev.gabrielsales.outboxworker.publisher.OutboxEventPublisher;
import dev.gabrielsales.outboxworker.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OutboxWorkerService {

    private static final Logger log = LoggerFactory.getLogger(OutboxWorkerService.class);

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    public OutboxWorkerService(OutboxEventRepository outboxEventRepository,
                               OutboxEventPublisher outboxEventPublisher) {
        this.outboxEventRepository = outboxEventRepository;
        this.outboxEventPublisher = outboxEventPublisher;
    }

    @Scheduled(fixedDelay = 5000)
    public void processPendingEvents() {
        var events = outboxEventRepository.findByProcessedFalse();

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} pending outbox events", events.size());

        for (var event : events) {
            try {
                outboxEventPublisher.publish(event);
                event.setProcessed(true);
                outboxEventRepository.save(event);
            } catch (Exception e) {
                log.error("Failed to process outbox event {}", event.getId(), e);
            }
        }
    }
}
