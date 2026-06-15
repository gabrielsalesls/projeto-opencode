package dev.gabrielsales.accountapi.controller;

import dev.gabrielsales.accountapi.dto.TransferRequest;
import dev.gabrielsales.accountapi.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@Valid @RequestBody TransferRequest request) {
        transferService.transfer(request.sourceAccountId(), request.destinationAccountId(), request.amount());
    }
}