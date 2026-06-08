package com.epsi.wealth.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Models.TransactionType;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.CategoryRepository;
import com.epsi.wealth.Repositories.TransactionRepository;
import java.util.List;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<TransactionModel> getAll() {
        return transactionRepository.findAll();
    }

    public TransactionModel getById(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction non trouvée"));
    }

    @Transactional
    public void delete(Long id) {
        TransactionModel transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction non trouvée"));
        AccountModel account = transaction.getAccount();
        if (transaction.getType() == TransactionType.REVENU) {
            account.setSoldeActuel(account.getSoldeActuel() - transaction.getMontant());
        } else {
            account.setSoldeActuel(account.getSoldeActuel() + transaction.getMontant());
        }
        accountRepository.save(account);
        transactionRepository.delete(transaction);
    }

    public List<TransactionModel> getByAccountId(Long accountId) {
        // 404 si le compte n'existe pas
        accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));
        return transactionRepository.findByAccountId(accountId);
    }

    @Transactional
    public TransactionModel create(TransactionModel transaction, Long accountId, Long categoryId) {
        AccountModel account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));

        com.epsi.wealth.Models.CategoryModel category = categoryRepository.findById(categoryId)
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
