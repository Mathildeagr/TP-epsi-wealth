package com.epsi.wealth.Models;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotEmpty(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    @NotEmpty(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    @Column(unique = true, nullable = false) 
    private String email;

    private LocalDate dateInscription = LocalDate.now();


}