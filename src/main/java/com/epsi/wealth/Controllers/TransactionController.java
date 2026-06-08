package com.epsi.wealth.Controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Models.TransactionType;
import com.epsi.wealth.Repositories.TransactionRepository;
import com.epsi.wealth.Services.TransactionService;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

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
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TransactionModel transaction, @RequestParam Long accountId, @RequestParam Long categoryId) {
        TransactionModel saved = transactionService.create(transaction, accountId, categoryId);

        String warning = null;
        
        if (saved.getType() == TransactionType.DEPENSE) {
            LocalDate now = LocalDate.now();
            // Calculer la somme des dépenses pour la catégorie ce mois-ci
            Double somme = transactionRepository.sumDepensesMois(
                saved.getCategory().getId(), TransactionType.DEPENSE, now.getMonthValue(), now.getYear());
            // Récupérer le plafond de la catégorie
            Double plafond = saved.getCategory().getPlafondMensuel();
            if (somme > plafond) {
                warning = String.format(
                    "Plafond dépassé pour la catégorie '%s'. Budget mensuel : %.2f€, Total après opération : %.2f€.",
                    saved.getCategory().getNom(), plafond, somme);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("transaction", saved);
        response.put("warning", warning);

        return ResponseEntity.ok(response);
    }
}
