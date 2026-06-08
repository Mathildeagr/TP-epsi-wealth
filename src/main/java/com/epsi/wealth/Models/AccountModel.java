package com.epsi.wealth.Models;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class AccountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "Le nom du compte est obligatoire")
    private String nom;

    @Column(nullable = false)
    private Double soldeActuel;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le taux d'intérêt doit être supérieur ou égal à 0")
    private Double tauxInteret;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private UserModel user;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionModel> transactions;

}

