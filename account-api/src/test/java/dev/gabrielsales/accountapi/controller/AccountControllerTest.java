package dev.gabrielsales.accountapi.controller;

import dev.gabrielsales.accountapi.dto.AccountResponse;
import dev.gabrielsales.accountapi.dto.CreateAccountRequest;
import dev.gabrielsales.accountapi.service.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    @DisplayName("create should delegate to service and return response")
    void create_should_callServiceAndReturnResponse_when_validRequest() {
        var request = new CreateAccountRequest("John Doe");
        var expectedResponse = new AccountResponse(UUID.randomUUID(), "John Doe", BigDecimal.ZERO, LocalDateTime.now());

        when(accountService.create(any(CreateAccountRequest.class))).thenReturn(expectedResponse);

        AccountResponse actualResponse = accountController.create(request);

        assertSame(expectedResponse, actualResponse);
        verify(accountService).create(request);
    }
}
