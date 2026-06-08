package com.epsi.wealth.Services;

import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.AccountType;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<AccountModel> getAll() {
        return accountRepository.findAll();
    }

    public List<AccountModel> getAllByUser(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public AccountModel getById(Long id) {
        return accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Compte non trouvé"));
    }

    public AccountModel createAccount (AccountModel account, Long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        account.setUser(user);
        return accountRepository.save(account);
    }

    public AccountModel update(Long id, AccountModel data) {
        AccountModel account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Compte non trouvé"));
        account.setNom(data.getNom());
        account.setTauxInteret(data.getTauxInteret());
        return accountRepository.save(account);
    }

    public record ProgressionDTO(int annee, double capital) {}

    public record ProjectionDTO(
        Long compteId,
        String nomCompte,
        double soldeActuel,
        String tauxInteret,
        int dureeAnnees,
        double capitalFinal,
        double gainsGeneres,
        String methode,
        List<ProgressionDTO> progression
    ) {}

    public ProjectionDTO getProjection(Long accountId, int annees) {
        AccountModel account = getById(accountId);

        if (account.getType() != AccountType.EPARGNE) {
            throw new IllegalArgumentException("La projection n'est disponible que pour les comptes d'épargne.");
        }
        if (annees < 1 || annees > 50) {
            throw new IllegalArgumentException("La durée doit être comprise entre 1 et 50 ans.");
        }

        double solde = account.getSoldeActuel();
        double taux  = account.getTauxInteret() / 100.0;

        List<ProgressionDTO> progression = new ArrayList<>();
        for (int i = 1; i <= annees; i++) {
            double capital = Math.round(solde * Math.pow(1 + taux, i) * 100.0) / 100.0;
            progression.add(new ProgressionDTO(i, capital));
        }

        double capitalFinal  = Math.round(solde * Math.pow(1 + taux, annees) * 100.0) / 100.0;
        double gainsGeneres  = Math.round((capitalFinal - solde) * 100.0) / 100.0;

        return new ProjectionDTO(
            account.getId(),
            account.getNom(),
            solde,
            account.getTauxInteret() + "%",
            annees,
            capitalFinal,
            gainsGeneres,
            "Intérêts composés annuels",
            progression
        );
    }
}
