package dev.gabrielsales.outboxworker.service;

import dev.gabrielsales.outboxworker.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OutboxWorkerService {

    private static final Logger log = LoggerFactory.getLogger(OutboxWorkerService.class);

    private final OutboxEventRepository outboxEventRepository;

    public OutboxWorkerService(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void processPendingEvents() {
        var events = outboxEventRepository.findByProcessedFalse();

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} pending outbox events", events.size());

        for (var event : events) {
            log.info("Processing event - id: {}, type: {}, payload: {}",
                    event.getId(), event.getEventType(), event.getPayload());
        }
    }
}
