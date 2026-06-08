package com.epsi.wealth.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epsi.wealth.Models.CategoryModel;
import com.epsi.wealth.Models.TransactionType;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

        @Query("""
        SELECT c.nom, SUM(t.montant), c.plafondMensuel
        FROM TransactionModel t
        JOIN t.category c
        JOIN t.account a
        WHERE a.user.id = :userId
        AND t.type = :type
        AND MONTH(t.transactionDate) = :mois
        AND YEAR(t.transactionDate) = :annee
        GROUP BY c.id, c.nom, c.plafondMensuel
        ORDER BY SUM(t.montant) DESC
        LIMIT 3""")
    List<Object[]> findTopSpendingByCategory(
        @Param("userId") Long userId,
        @Param("type") TransactionType type,
        @Param("mois") int mois,
        @Param("annee") int annee
    );

    List<CategoryModel> findByUserId(Long userId);
}