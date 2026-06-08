package com.epsi.wealth.Models;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Le libellé ne peut pas être vide")
    private String libelle;

    @Column(nullable = false)
    @Positive(message = "Le montant doit être strictement positif")
    private Double montant;

    @NotNull(message = "La date ne peut pas être nulle")
    private LocalDate transactionDate;

    @NotNull(message = "Le type de transaction ne peut pas être nul")
    private TransactionType type;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AccountModel account;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CategoryModel category;



}