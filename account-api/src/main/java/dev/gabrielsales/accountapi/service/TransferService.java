package dev.gabrielsales.accountapi.service;

import dev.gabrielsales.accountapi.entity.Account;
import dev.gabrielsales.accountapi.entity.Transfer;
import dev.gabrielsales.accountapi.repository.AccountRepository;
import dev.gabrielsales.accountapi.repository.TransferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public void transfer(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        var ids = java.util.stream.Stream.of(sourceAccountId, destinationAccountId)
                .sorted()
                .toList();

        var firstAccount = accountRepository.findByIdWithLock(ids.get(0))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        var secondAccount = accountRepository.findByIdWithLock(ids.get(1))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        Account sourceAccount;
        Account destinationAccount;

        if (sourceAccountId.equals(firstAccount.getId())) {
            sourceAccount = firstAccount;
            destinationAccount = secondAccount;
        } else {
            sourceAccount = secondAccount;
            destinationAccount = firstAccount;
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        var transfer = new Transfer();
        transfer.setId(UUID.randomUUID());
        transfer.setSourceAccountId(sourceAccountId);
        transfer.setDestinationAccountId(destinationAccountId);
        transfer.setAmount(amount);
        transfer.setCreatedAt(LocalDateTime.now());

        transferRepository.save(transfer);
    }
}