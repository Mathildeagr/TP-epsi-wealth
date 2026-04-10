package com.epsi.wealth.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epsi.wealth.Models.AccountModel;

public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    // Somme de tous les soldes d'un user
    @Query("SELECT COALESCE(SUM(a.soldeActuel), 0) FROM AccountModel a WHERE a.user.id = :userId")
    Double sumSoldeByUser(@Param("userId") Long userId);

    // Bénéfice annuel projeté (comptes EPARGNE uniquement)
    @Query("SELECT COALESCE(SUM(a.soldeActuel * a.tauxInteret / 100), 0) FROM AccountModel a WHERE a.user.id = :userId AND a.type = 'EPARGNE'")
    Double beneficeAnnuelProjetee(@Param("userId") Long userId);
}
