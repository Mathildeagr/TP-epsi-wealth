package com.epsi.wealth.Services;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Models.TransactionType;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.TransactionRepository;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Transactional pour garantir que les opérations sur la base de données sont atomiques
    @Transactional
    public TransactionModel create(TransactionModel transaction) {
    AccountModel account = transaction.getAccount();
    if (transaction.getType() == TransactionType.REVENU) {
        account.setSoldeActuel(account.getSoldeActuel() + transaction.getMontant());
    } else {
        account.setSoldeActuel(account.getSoldeActuel() - transaction.getMontant());
    }
    accountRepository.save(account);
    return transactionRepository.save(transaction);
}

}
