package dev.gabrielsales.accountapi.service;

import dev.gabrielsales.accountapi.dto.AccountResponse;
import dev.gabrielsales.accountapi.dto.CreateAccountRequest;
import dev.gabrielsales.accountapi.entity.Account;
import dev.gabrielsales.accountapi.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("create should save account and return AccountResponse with zero balance")
    void create_should_saveAccountAndReturnResponse_when_validRequest() {
        var request = new CreateAccountRequest("John Doe");
        var savedAccount = new Account();
        var accountId = UUID.randomUUID();
        savedAccount.setId(accountId);
        savedAccount.setOwnerName("John Doe");
        savedAccount.setBalance(BigDecimal.ZERO);
        savedAccount.setCreatedAt(LocalDateTime.now());

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountResponse response = accountService.create(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(accountId, response.id()),
                () -> assertEquals("John Doe", response.ownerName()),
                () -> assertEquals(BigDecimal.ZERO, response.balance()),
                () -> assertNotNull(response.createdAt())
        );

        verify(accountRepository).save(any(Account.class));
    }
}
