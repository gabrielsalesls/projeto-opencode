package dev.gabrielsales.accountapi.controller;

import dev.gabrielsales.accountapi.dto.AccountResponse;
import dev.gabrielsales.accountapi.dto.CreateAccountRequest;
import dev.gabrielsales.accountapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.create(request);
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable UUID id) {
        return accountService.findById(id);
    }
}
