package com.epsi.wealth.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epsi.wealth.Models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    boolean existsByEmail(String email);
}
