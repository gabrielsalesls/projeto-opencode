package dev.gabrielsales.accountapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(@NotBlank String ownerName) {
}
