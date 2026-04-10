package com.epsi.wealth.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epsi.wealth.Models.TransactionModel;


@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {
    // JpaRepository fournit déjà les méthodes CRUD de base
    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM TransactionModel t " +
       "WHERE t.category.id = :categoryId AND t.type = 'DEPENSE' " +
       "AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
        Double sumDepensesMois(@Param("categoryId") Long categoryId,
                       @Param("month") int month,
                       @Param("year") int year);
}