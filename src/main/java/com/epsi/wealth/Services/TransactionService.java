package com.epsi.wealth.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Models.TransactionType;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.CategoryRepository;
import com.epsi.wealth.Repositories.TransactionRepository;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public TransactionModel create(TransactionModel transaction) {
        AccountModel account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        com.epsi.wealth.Models.CategoryModel category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        if (transaction.getType() == TransactionType.REVENU) {
            account.setSoldeActuel(account.getSoldeActuel() + transaction.getMontant());
        } else {
            account.setSoldeActuel(account.getSoldeActuel() - transaction.getMontant());
        }
        accountRepository.save(account);
        transaction.setAccount(account);
        transaction.setCategory(category);
        return transactionRepository.save(transaction);
    }

}
