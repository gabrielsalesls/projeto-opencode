package dev.gabrielsales.accountapi.controller;

import dev.gabrielsales.accountapi.dto.TransferRequest;
import dev.gabrielsales.accountapi.service.TransferService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @Mock
    private TransferService transferService;

    @InjectMocks
    private TransferController transferController;

    @Test
    @DisplayName("transfer should delegate to service when valid request")
    void transfer_should_callService_when_validRequest() {
        var sourceAccountId = UUID.randomUUID();
        var destinationAccountId = UUID.randomUUID();
        var amount = new BigDecimal("100.00");
        var request = new TransferRequest(sourceAccountId, destinationAccountId, amount);

        doNothing().when(transferService).transfer(eq(sourceAccountId), eq(destinationAccountId), eq(amount));

        transferController.transfer(request);

        verify(transferService).transfer(sourceAccountId, destinationAccountId, amount);
    }
}