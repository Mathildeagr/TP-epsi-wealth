package com.epsi.wealth.Services;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import com.epsi.wealth.Exceptions.EmailAlreadyExistsException;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
}
