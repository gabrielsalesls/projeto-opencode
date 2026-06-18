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
}
