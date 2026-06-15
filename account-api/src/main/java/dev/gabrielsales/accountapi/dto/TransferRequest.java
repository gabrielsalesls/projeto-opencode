package dev.gabrielsales.accountapi.dto;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(
    UUID sourceAccountId,
    UUID destinationAccountId,
    @Positive BigDecimal amount
) {
}