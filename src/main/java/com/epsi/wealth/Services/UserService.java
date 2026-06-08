package com.epsi.wealth.Services;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.epsi.wealth.Exceptions.EmailAlreadyExistsException;
import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Models.AccountModel;
import com.epsi.wealth.Models.TransactionType;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Repositories.AccountRepository;
import com.epsi.wealth.Repositories.CategoryRepository;
import com.epsi.wealth.Repositories.TransactionRepository;
import com.epsi.wealth.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
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

    public List<UserModel> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        userRepository.deleteById(id);
    }

    public List<TransactionModel> getTransactionsByUser(Long userId, Integer mois, Integer annee) {
        getUserById(userId);
        return transactionRepository.findByUserFiltered(userId, mois, annee);
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

    // DTO pour le matelas de sécurité
    public record SafetyBufferDTO(Long userId, int moisAnalyses, String methodologie, Double matelas) {}

    public SafetyBufferDTO getSafetyBuffer(Long userId) {
        UserModel user = getUserById(userId);
        LocalDate today = LocalDate.now();
        LocalDate inscription = user.getDateInscription();

        // Calcul du nombre de mois entre l'inscription et aujourd'hui, minimum 1 pour éviter la division par zéro
        long nbMois = Math.max(ChronoUnit.MONTHS.between(inscription.withDayOfMonth(1), today.withDayOfMonth(1)), 1);

        double matelas;
        int moisAnalyses;
        String methodologie;

        // Si l'utilisateur est inscrit depuis plus d'un an, on prend les 12 derniers mois réels
        if (nbMois >= 12) {
            moisAnalyses = 12;
            double total = transactionRepository.sumDepensesDepuis(userId, TransactionType.DEPENSE, today.minusMonths(12));
            matelas = total;
            methodologie = "Basé sur les 12 derniers mois réels";
        // Sinon, on projette sur 12 mois en utilisant la moyenne mensuelle des dépenses depuis l'inscription
        } else {
            moisAnalyses = (int) nbMois;
            double total = transactionRepository.sumDepensesDepuis(userId, TransactionType.DEPENSE, inscription);
            matelas = (total / moisAnalyses) * 12;
            methodologie = "Projection sur 12 mois (historique insuffisant)";
        }

        return new SafetyBufferDTO(userId, moisAnalyses, methodologie, matelas);
    }

    public record Advisor(String statut, Float soldeTotal, Float matelasRequis, Float surplus, String message, List<AccountModel> accounts ) {
}
    public Advisor getAdvisor (Long userId) {
        UserModel user = getUserById(userId);
        SafetyBufferDTO safetyBuffer = getSafetyBuffer(userId);
        double matelas = safetyBuffer.matelas;
        double soldeTotal = getDashboard(userId).soldeTotal;
        List<AccountModel> accounts = accountRepository.findSavingAccountByUserId(user.getId());


        //Matelas incomplet
        if (soldeTotal <= matelas) {
            return new Advisor(
                "MATELAS_INCOMPLET", 
                (float) soldeTotal, 
                (float) matelas, 
                (float) (soldeTotal - matelas), 
                String.format("Priorité : constituez votre matelas. Il vous manque %.2f€.", matelas - soldeTotal),
                accounts
            );  
        } else {
            return new Advisor(
                "MATELAS_OK", 
                (float) soldeTotal, 
                (float) matelas, 
                (float) (soldeTotal - matelas),
                null, 
                accounts
            );
        }
        
    }

    public record TopSpendingByCategory(String category, double totalAmount, double limit, boolean isExceeded){}

    public List<TopSpendingByCategory> getTopSpendingByCategory(Long userId, int mois, int annee) {
       getUserById(userId);
       List<Object[]> topSpendingByCategory = categoryRepository.findTopSpendingByCategory(userId, TransactionType.DEPENSE, mois, annee);
       List<TopSpendingByCategory> topSpendingByCategoryList = new ArrayList<>();
       for (Object[] row : topSpendingByCategory) {
           String category = (String) row[0];
           double totalAmount = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
           double limit = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
           boolean isExceeded = limit > 0 && totalAmount > limit;
           topSpendingByCategoryList.add(new TopSpendingByCategory(category, totalAmount, limit, isExceeded));
       }
       return topSpendingByCategoryList;
    }
}
