package com.epsi.wealth.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epsi.wealth.Models.TransactionModel;
import com.epsi.wealth.Models.TransactionType;
 

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {
    // JpaRepository fournit déjà les méthodes CRUD de base

    // Somme des dépenses pour une catégorie donnée sur un mois spécifique
    @Query(
        // COALESCE pour retourner 0 au lieu de null si aucune dépense n'est trouvée
        "SELECT COALESCE(SUM(t.montant), 0) FROM TransactionModel t " +
       "WHERE t.category.id = :categoryId AND t.type = :type " +
       "AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year"
    )
    Double sumDepensesMois(
        @Param("categoryId") Long categoryId,
        @Param("type") TransactionType type,
        @Param("month") int month,
        @Param("year") int year
    );

    // SUM par type (REVENU ou DEPENSE) pour le mois en cours
    @Query(
        "SELECT COALESCE(SUM(t.montant), 0) FROM TransactionModel t " +
        "WHERE t.account.user.id = :userId AND t.type = :type " +
        "AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year"
    )
    Double sumParTypeMois(
        @Param("userId") Long userId, @Param("type") TransactionType type,
        @Param("month") int month, @Param("year") int year
    );

    // Somme des DEPENSES d'un utilisateur depuis une date donnée
    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM TransactionModel t " +
           "WHERE t.account.user.id = :userId AND t.type = :type AND t.transactionDate >= :fromDate")
    Double sumDepensesDepuis(@Param("userId") Long userId, @Param("type") TransactionType type, @Param("fromDate") LocalDate fromDate);

    // Transactions d'un utilisateur filtrées par mois et/ou année (JPQL, tri date décroissante)
    @Query("SELECT t FROM TransactionModel t WHERE t.account.user.id = :userId " +
           "AND (:mois IS NULL OR MONTH(t.transactionDate) = :mois) " +
           "AND (:annee IS NULL OR YEAR(t.transactionDate) = :annee) " +
           "ORDER BY t.transactionDate DESC")
    List<TransactionModel> findByUserFiltered(
        @Param("userId") Long userId,
        @Param("mois") Integer mois,
        @Param("annee") Integer annee
    );

    // Toutes les transactions d'un compte donné
    List<TransactionModel> findByAccountId(Long accountId);

}