package com.epsi.wealth.Models;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    @Column(nullable = false)
    private Double montant;

    private LocalDate date;

    private TransactionType type;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private AccountModel account;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CategoryModel category;



}