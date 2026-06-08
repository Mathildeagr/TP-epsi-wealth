package com.epsi.wealth.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Services.AccountService;
import com.epsi.wealth.Services.TransactionService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    private Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping
    public List<AccountModel> getAll() {
        return accountService.getAll();
    }

    @GetMapping("/{id}")
    public AccountModel getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @PostMapping
    public AccountModel createAccount(@Valid @RequestBody AccountModel account, @RequestParam Long userId) {
        if (!userId.equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return accountService.createAccount(account, userId);
    }

    @PutMapping("/{id}")
    public AccountModel update(@PathVariable Long id, @Valid @RequestBody AccountModel account) {
        AccountModel existing = accountService.getById(id);
        if (!existing.getUser().getId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return accountService.update(id, account);
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionModel> getTransactions(@PathVariable Long id) {
        return transactionService.getByAccountId(id);
    }
    
    @GetMapping("/{id}/projection")
    public AccountService.ProjectionDTO getProjection(@PathVariable Long id, @RequestParam int annees) {
        return accountService.getProjection(id, annees);
    }
}
