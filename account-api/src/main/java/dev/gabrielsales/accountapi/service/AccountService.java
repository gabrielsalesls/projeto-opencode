package dev.gabrielsales.accountapi.service;

import dev.gabrielsales.accountapi.dto.AccountResponse;
import dev.gabrielsales.accountapi.dto.CreateAccountRequest;
import dev.gabrielsales.accountapi.entity.Account;
import dev.gabrielsales.accountapi.repository.AccountRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse create(CreateAccountRequest request) {
        var account = new Account();
        account.setId(UUID.randomUUID());
        account.setOwnerName(request.ownerName());
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());

        var saved = accountRepository.save(account);
        return AccountResponse.fromEntity(saved);
    }
}
