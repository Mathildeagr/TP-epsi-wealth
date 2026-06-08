package com.epsi.wealth.Services;

import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.CategoryModel;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Models.TransactionType;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.CategoryRepository;
import com.epsi.wealth.Repositories.TransactionRepository;
import java.time.LocalDate;
import java.util.List;
import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    public record CreateTransactionResult(TransactionModel transaction, String warning) {}

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TransactionModel> getAll() {
        return transactionRepository.findAll();
    }

    public List<TransactionModel> getAllByUser(Long userId) {
        return transactionRepository.findByUserFiltered(userId, null, null);
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
    public CreateTransactionResult create(TransactionModel transaction, Long accountId, Long categoryId) {
        AccountModel account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        CategoryModel category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        if (transaction.getType() == TransactionType.REVENU) {
            account.setSoldeActuel(account.getSoldeActuel() + transaction.getMontant());
        } else {
            account.setSoldeActuel(account.getSoldeActuel() - transaction.getMontant());
        }
        accountRepository.save(account);
        transaction.setAccount(account);
        transaction.setCategory(category);
        TransactionModel saved = transactionRepository.save(transaction);

        String warning = null;
        if (saved.getType() == TransactionType.DEPENSE) {
            LocalDate now = LocalDate.now();
            Double somme = transactionRepository.sumDepensesMois(
                saved.getCategory().getId(), TransactionType.DEPENSE, now.getMonthValue(), now.getYear());
            Double plafond = saved.getCategory().getPlafondMensuel();
            if (somme > plafond) {
                warning = String.format(
                    "Plafond dépassé pour la catégorie '%s'. Budget mensuel : %.2f€, Total après opération : %.2f€.",
                    saved.getCategory().getNom(), plafond, somme);
            }
        }

        return new CreateTransactionResult(saved, warning);
    }

}
