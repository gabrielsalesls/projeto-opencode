package dev.gabrielsales.accountapi.dto;

import dev.gabrielsales.accountapi.entity.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountResponseTest {

    @Test
    @DisplayName("fromEntity should map all fields correctly")
    void fromEntity_should_mapAllFields_when_validAccount() {
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();
        var account = new Account();
        account.setId(id);
        account.setOwnerName("Jane Doe");
        account.setBalance(new BigDecimal("150.00"));
        account.setCreatedAt(now);

        var response = AccountResponse.fromEntity(account);

        assertAll(
                () -> assertEquals(id, response.id()),
                () -> assertEquals("Jane Doe", response.ownerName()),
                () -> assertEquals(new BigDecimal("150.00"), response.balance()),
                () -> assertEquals(now, response.createdAt())
        );
    }
}
