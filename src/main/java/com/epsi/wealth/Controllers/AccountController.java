package com.epsi.wealth.Controllers;

import org.springframework.web.bind.annotation.*;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Services.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping ("api/accounts")
public class AccountController {
    private final AccountService accountService;
    public AccountController(@Valid AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountModel createAccount(@Valid @RequestBody AccountModel account, @RequestParam Long userId) {
        return accountService.createAccount(account, userId);
    }
}


