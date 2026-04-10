package com.epsi.wealth.Services;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import com.epsi.wealth.Exceptions.EmailAlreadyExistsException;
import com.epsi.wealth.Models.TransactionType;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.TransactionRepository;
import com.epsi.wealth.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    

    public UserService(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // Création d'un utilisateur avec validation de l'email et gestion des doublons
    public UserModel createUser(UserModel user) {

        if(user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new RuntimeException("L'email est obligatoire");
        }

        if(userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Un compte existe déjà avec l'adresse : " + user.getEmail());
        }

        user.setDateInscription(LocalDate.now());
        return userRepository.save(user);
    }

    // Récupération d'un utilisateur par son ID avec gestion de l'absence d'utilisateur
    public UserModel getUserById (Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Utilisateur non rencontré")); 
    }

    // DTO pour le dashboard
    public record DashboardDTO(
        Double soldeTotal,
        Double totalRevenus,
        Double totalDepenses,
        Double deltaRevenusDepenses,
        Double beneficeAnnuelProjetee
    ) {}

    // Récupération des données du dashboard pour un utilisateur donné
    public DashboardDTO getDashboard(Long userId) {
    LocalDate now = LocalDate.now();
    double soldeTotal = accountRepository.sumSoldeByUser(userId);
    double revenus = transactionRepository.sumParTypeMois(userId, TransactionType.REVENU, now.getMonthValue(), now.getYear());
    double depenses = transactionRepository.sumParTypeMois(userId, TransactionType.DEPENSE, now.getMonthValue(), now.getYear());
    double benefice = accountRepository.beneficeAnnuelProjetee(userId);

    return new DashboardDTO(soldeTotal, revenus, depenses, revenus - depenses, benefice);
}
}
