package dev.gabrielsales.accountapi.dto;

import dev.gabrielsales.accountapi.entity.Account;
import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponse(UUID accountId, BigDecimal balance) {

    public static BalanceResponse fromEntity(Account account) {
        return new BalanceResponse(account.getId(), account.getBalance());
    }
}
