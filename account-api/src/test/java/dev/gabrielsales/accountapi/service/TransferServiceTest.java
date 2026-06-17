package dev.gabrielsales.accountapi.service;

import dev.gabrielsales.accountapi.entity.Account;
import dev.gabrielsales.accountapi.entity.OutboxEvent;
import dev.gabrielsales.accountapi.entity.Transfer;
import dev.gabrielsales.accountapi.repository.AccountRepository;
import dev.gabrielsales.accountapi.repository.OutboxEventRepository;
import dev.gabrielsales.accountapi.repository.TransferRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    @DisplayName("transfer should debit source account, credit destination account, and save transfer when valid")
    void transfer_should_debitSourceCreditDestinationAndSaveTransfer_when_valid() {
        var sourceAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var destinationAccountId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var amount = new BigDecimal("100.00");
        var sourceBalance = new BigDecimal("500.00");
        var destinationBalance = new BigDecimal("200.00");

        var sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setOwnerName("Source Account");
        sourceAccount.setBalance(sourceBalance);
        sourceAccount.setCreatedAt(LocalDateTime.now());

        var destinationAccount = new Account();
        destinationAccount.setId(destinationAccountId);
        destinationAccount.setOwnerName("Destination Account");
        destinationAccount.setBalance(destinationBalance);
        destinationAccount.setCreatedAt(LocalDateTime.now());

        var savedTransfer = new Transfer();
        var transferId = UUID.randomUUID();
        savedTransfer.setId(transferId);
        savedTransfer.setSourceAccountId(sourceAccountId);
        savedTransfer.setDestinationAccountId(destinationAccountId);
        savedTransfer.setAmount(amount);
        savedTransfer.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findByIdWithLock(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByIdWithLock(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);
        when(outboxEventRepository.save(any(OutboxEvent.class))).thenAnswer(invocation -> invocation.getArgument(0));

        transferService.transfer(sourceAccountId, destinationAccountId, amount);

        assertAll(
                () -> assertEquals(sourceBalance.subtract(amount), sourceAccount.getBalance()),
                () -> assertEquals(destinationBalance.add(amount), destinationAccount.getBalance())
        );

        verify(accountRepository, times(2)).findByIdWithLock(any(UUID.class));
        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(destinationAccount);
        verify(transferRepository).save(any(Transfer.class));

        var captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());
        var savedEvent = captor.getValue();
        assertEquals("TRANSFER_CREATED", savedEvent.getEventType());
        assertFalse(savedEvent.isProcessed());
        assertNotNull(savedEvent.getPayload());
        assertTrue(savedEvent.getPayload().contains("\"transferId\""));
        assertTrue(savedEvent.getPayload().contains("\"sourceAccountId\""));
        assertTrue(savedEvent.getPayload().contains("\"destinationAccountId\""));
        assertTrue(savedEvent.getPayload().contains("\"amount\""));
    }

    @Test
    @DisplayName("transfer should throw 404 when source account not found")
    void transfer_should_throw404_when_sourceAccountNotFound() {
        var sourceAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var destinationAccountId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var amount = new BigDecimal("100.00");

        when(accountRepository.findByIdWithLock(sourceAccountId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> transferService.transfer(sourceAccountId, destinationAccountId, amount));

        verify(accountRepository).findByIdWithLock(sourceAccountId);
        verify(accountRepository, never()).findByIdWithLock(destinationAccountId);
        verify(transferRepository, never()).save(any(Transfer.class));
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    @DisplayName("transfer should throw 404 when destination account not found")
    void transfer_should_throw404_when_destinationAccountNotFound() {
        var sourceAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var destinationAccountId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var amount = new BigDecimal("100.00");

        var sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setOwnerName("Source Account");
        sourceAccount.setBalance(new BigDecimal("500.00"));
        sourceAccount.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findByIdWithLock(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByIdWithLock(destinationAccountId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> transferService.transfer(sourceAccountId, destinationAccountId, amount));

        verify(accountRepository).findByIdWithLock(sourceAccountId);
        verify(accountRepository).findByIdWithLock(destinationAccountId);
        verify(transferRepository, never()).save(any(Transfer.class));
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }

    @Test
    @DisplayName("transfer should throw 400 when source account has insufficient balance")
    void transfer_should_throw400_when_insufficientBalance() {
        var sourceAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var destinationAccountId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        var amount = new BigDecimal("100.00");
        var sourceBalance = new BigDecimal("50.00");

        var sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setOwnerName("Source Account");
        sourceAccount.setBalance(sourceBalance);
        sourceAccount.setCreatedAt(LocalDateTime.now());

        var destinationAccount = new Account();
        destinationAccount.setId(destinationAccountId);
        destinationAccount.setOwnerName("Destination Account");
        destinationAccount.setBalance(new BigDecimal("200.00"));
        destinationAccount.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findByIdWithLock(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByIdWithLock(destinationAccountId)).thenReturn(Optional.of(destinationAccount));

        assertThrows(ResponseStatusException.class,
                () -> transferService.transfer(sourceAccountId, destinationAccountId, amount));

        verify(accountRepository).findByIdWithLock(sourceAccountId);
        verify(accountRepository).findByIdWithLock(destinationAccountId);
        verify(transferRepository, never()).save(any(Transfer.class));
        verify(outboxEventRepository, never()).save(any(OutboxEvent.class));
    }
}