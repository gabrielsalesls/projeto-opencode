package dev.gabrielsales.accountapi.service;

import dev.gabrielsales.accountapi.entity.Account;
import dev.gabrielsales.accountapi.entity.Transfer;
import dev.gabrielsales.accountapi.repository.AccountRepository;
import dev.gabrielsales.accountapi.repository.TransferRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private TransferService transferService;

    @Test
    @DisplayName("transfer should debit source account, credit destination account, and save transfer when valid")
    void transfer_should_debitSourceCreditDestinationAndSaveTransfer_when_valid() {
        var sourceAccountId = UUID.randomUUID();
        var destinationAccountId = UUID.randomUUID();
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

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(any(Account.class));
        when(transferRepository.save(any(Transfer.class))).thenReturn(savedTransfer);

        transferService.transfer(sourceAccountId, destinationAccountId, amount);

        assertAll(
                () -> assertEquals(sourceBalance.subtract(amount), sourceAccount.getBalance()),
                () -> assertEquals(destinationBalance.add(amount), destinationAccount.getBalance()),
                () -> assertEquals(2, accountRepository.save.callCount)
        );

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository).findById(destinationAccountId);
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    @DisplayName("transfer should throw 404 when source account not found")
    void transfer_should_throw404_when_sourceAccountNotFound() {
        var sourceAccountId = UUID.randomUUID();
        var destinationAccountId = UUID.randomUUID();
        var amount = new BigDecimal("100.00");

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> transferService.transfer(sourceAccountId, destinationAccountId, amount));

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository, never()).findById(destinationAccountId);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    @DisplayName("transfer should throw 404 when destination account not found")
    void transfer_should_throw404_when_destinationAccountNotFound() {
        var sourceAccountId = UUID.randomUUID();
        var destinationAccountId = UUID.randomUUID();
        var amount = new BigDecimal("100.00");

        var sourceAccount = new Account();
        sourceAccount.setId(sourceAccountId);
        sourceAccount.setOwnerName("Source Account");
        sourceAccount.setBalance(new BigDecimal("500.00"));
        sourceAccount.setCreatedAt(LocalDateTime.now());

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> transferService.transfer(sourceAccountId, destinationAccountId, amount));

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository).findById(destinationAccountId);
        verify(transferRepository, never()).save(any(Transfer.class));
    }

    @Test
    @DisplayName("transfer should throw 400 when source account has insufficient balance")
    void transfer_should_throw400_when_insufficientBalance() {
        var sourceAccountId = UUID.randomUUID();
        var destinationAccountId = UUID.randomUUID();
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

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(destinationAccountId)).thenReturn(Optional.of(destinationAccount));

        assertThrows(ResponseStatusException.class,
                () -> transferService.transfer(sourceAccountId, destinationAccountId, amount));

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository).findById(destinationAccountId);
        verify(transferRepository, never()).save(any(Transfer.class));
    }
}