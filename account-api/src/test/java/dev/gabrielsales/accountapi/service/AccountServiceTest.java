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
import java.util.Optional;

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

    @Test
    @DisplayName("findById should return AccountResponse when account exists")
    void findById_should_returnAccountResponse_when_accountExists() {
        var accountId = UUID.randomUUID();
        var account = new Account();
        account.setId(accountId);
        account.setOwnerName("Jane Doe");
        account.setBalance(new BigDecimal("250.00"));
        account.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountResponse response = accountService.findById(accountId);

        assertAll(
                () -> assertEquals(accountId, response.id()),
                () -> assertEquals("Jane Doe", response.ownerName()),
                () -> assertEquals(new BigDecimal("250.00"), response.balance()),
                () -> assertNotNull(response.createdAt())
        );

        verify(accountRepository).findById(accountId);
    }

    @Test
    @DisplayName("findById should throw 404 when account does not exist")
    void findById_should_throw404_when_accountNotFound() {
        var accountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> accountService.findById(accountId));

        verify(accountRepository).findById(accountId);
    }
}
