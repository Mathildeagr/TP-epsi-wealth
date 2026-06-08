package com.epsi.wealth.Controllers;

import org.springframework.web.bind.annotation.*;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Services.AccountService;
import com.epsi.wealth.Services.TransactionService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping ("api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
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
        return accountService.createAccount(account, userId);
    }

    @PutMapping("/{id}")
    public AccountModel update(@PathVariable Long id, @Valid @RequestBody AccountModel account) {
        return accountService.update(id, account);
    }

    @GetMapping("/{id}/transactions")
    public List<TransactionModel> getTransactions(@PathVariable Long id) {
        return transactionService.getByAccountId(id);
    }
}


