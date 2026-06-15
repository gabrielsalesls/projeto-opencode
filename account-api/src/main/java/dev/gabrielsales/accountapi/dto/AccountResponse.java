package dev.gabrielsales.accountapi.dto;

import dev.gabrielsales.accountapi.entity.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(UUID id, String ownerName, BigDecimal balance, LocalDateTime createdAt) {

    public static AccountResponse fromEntity(Account account) {
        return new AccountResponse(account.getId(), account.getOwnerName(), account.getBalance(), account.getCreatedAt());
    }
}
