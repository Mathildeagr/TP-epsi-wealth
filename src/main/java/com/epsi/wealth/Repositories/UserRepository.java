package com.epsi.wealth.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epsi.wealth.Models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    boolean existsByEmail(String email);
    Optional<UserModel> findByEmail(String email);
}
