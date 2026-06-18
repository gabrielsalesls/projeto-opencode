package dev.gabrielsales.outboxworker.service;

import dev.gabrielsales.outboxworker.entity.OutboxEvent;
import dev.gabrielsales.outboxworker.publisher.OutboxEventPublisher;
import dev.gabrielsales.outboxworker.repository.OutboxEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxWorkerServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private OutboxEventPublisher outboxEventPublisher;

    @InjectMocks
    private OutboxWorkerService outboxWorkerService;

    @Test
    @DisplayName("processPendingEvents should publish each pending event when events exist")
    void processPendingEvents_shouldPublishEachEvent_when_eventsExist() {
        var event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setEventType("TRANSFER_CREATED");
        event.setPayload("{}");
        event.setProcessed(false);
        event.setCreatedAt(LocalDateTime.now());

        when(outboxEventRepository.findByProcessedFalse()).thenReturn(List.of(event));

        outboxWorkerService.processPendingEvents();

        verify(outboxEventRepository).findByProcessedFalse();
        verify(outboxEventPublisher).publish(event);
        verify(outboxEventRepository).save(event);
        assertTrue(event.isProcessed());
    }

    @Test
    @DisplayName("processPendingEvents should do nothing when no pending events")
    void processPendingEvents_shouldDoNothing_when_noPendingEvents() {
        when(outboxEventRepository.findByProcessedFalse()).thenReturn(List.of());

        outboxWorkerService.processPendingEvents();

        verify(outboxEventRepository).findByProcessedFalse();
        verify(outboxEventPublisher, never()).publish(any());
        verify(outboxEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("processPendingEvents should continue processing remaining events when one fails")
    void processPendingEvents_shouldContinueProcessing_whenOneEventFails() {
        var event1 = createEvent();
        var event2 = createEvent();
        var event3 = createEvent();

        when(outboxEventRepository.findByProcessedFalse()).thenReturn(List.of(event1, event2, event3));
        lenient().doThrow(new RuntimeException("RabbitMQ down")).when(outboxEventPublisher).publish(event2);

        outboxWorkerService.processPendingEvents();

        verify(outboxEventPublisher).publish(event1);
        verify(outboxEventPublisher).publish(event2);
        verify(outboxEventPublisher).publish(event3);

        verify(outboxEventRepository).save(event1);
        verify(outboxEventRepository, never()).save(event2);
        verify(outboxEventRepository).save(event3);

        assertTrue(event1.isProcessed());
        assertFalse(event2.isProcessed());
        assertTrue(event3.isProcessed());
    }

    @Test
    @DisplayName("processPendingEvents should not mark event as processed when publish fails")
    void processPendingEvents_shouldNotMarkAsProcessed_whenPublishFails() {
        var event = createEvent();

        when(outboxEventRepository.findByProcessedFalse()).thenReturn(List.of(event));
        doThrow(new RuntimeException("RabbitMQ down")).when(outboxEventPublisher).publish(event);

        outboxWorkerService.processPendingEvents();

        verify(outboxEventPublisher).publish(event);
        verify(outboxEventRepository, never()).save(event);
        assertFalse(event.isProcessed());
    }

    private OutboxEvent createEvent() {
        var event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setEventType("TRANSFER_CREATED");
        event.setPayload("{}");
        event.setProcessed(false);
        event.setCreatedAt(LocalDateTime.now());
        return event;
    }
}
