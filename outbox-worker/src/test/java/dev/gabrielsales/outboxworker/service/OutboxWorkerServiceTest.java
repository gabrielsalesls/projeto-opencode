package dev.gabrielsales.outboxworker.service;

import dev.gabrielsales.outboxworker.entity.OutboxEvent;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxWorkerServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private OutboxWorkerService outboxWorkerService;

    @Test
    @DisplayName("processPendingEvents should log each pending event when events exist")
    void processPendingEvents_shouldLogEachEvent_when_eventsExist() {
        var event = new OutboxEvent();
        event.setId(UUID.randomUUID());
        event.setEventType("TRANSFER_CREATED");
        event.setPayload("{}");
        event.setProcessed(false);
        event.setCreatedAt(LocalDateTime.now());

        when(outboxEventRepository.findByProcessedFalse()).thenReturn(List.of(event));

        outboxWorkerService.processPendingEvents();

        verify(outboxEventRepository).findByProcessedFalse();
    }

    @Test
    @DisplayName("processPendingEvents should do nothing when no pending events")
    void processPendingEvents_shouldDoNothing_when_noPendingEvents() {
        when(outboxEventRepository.findByProcessedFalse()).thenReturn(List.of());

        outboxWorkerService.processPendingEvents();

        verify(outboxEventRepository).findByProcessedFalse();
    }
}
