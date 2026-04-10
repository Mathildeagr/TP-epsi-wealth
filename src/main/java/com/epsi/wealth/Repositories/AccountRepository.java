package com.epsi.wealth.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epsi.wealth.Models.AccountModel;

public interface AccountRepository extends JpaRepository<AccountModel, Long> {
}
