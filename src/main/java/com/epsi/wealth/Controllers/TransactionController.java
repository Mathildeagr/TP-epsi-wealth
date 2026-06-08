package com.epsi.wealth.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.List;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Services.AccountService;
import com.epsi.wealth.Services.TransactionService;
import com.epsi.wealth.Services.TransactionService.CreateTransactionResult;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService, AccountService accountService) {
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    private Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping
    public List<TransactionModel> getAll() {
        return transactionService.getAll();
    }

    @GetMapping("/{id}")
    public TransactionModel getById(@PathVariable Long id) {
        return transactionService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        TransactionModel transaction = transactionService.getById(id);
        if (!transaction.getAccount().getUser().getId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CreateTransactionResult> create(
            @Valid @RequestBody TransactionModel transaction,
            @RequestParam Long accountId,
            @RequestParam Long categoryId) {
        AccountModel account = accountService.getById(accountId);
        if (!account.getUser().getId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return ResponseEntity.ok(transactionService.create(transaction, accountId, categoryId));
    }
}
