package com.epsi.wealth.Models;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom et prénom ne sont doivent pas être null
    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    // email doit être unique et ne peut pas être null
    @Column(unique = true, nullable = false) 
    private String email;

    // date Inscription par défaut à la date actuelle
    private LocalDate dateInscription = LocalDate.now();


}