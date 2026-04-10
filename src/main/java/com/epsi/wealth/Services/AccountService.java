package com.epsi.wealth.Services;

import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.UserRepository;

@Service
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public AccountModel createAccount (AccountModel account, Long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        account.setUser(user);
        return accountRepository.save(account);
    }
}
