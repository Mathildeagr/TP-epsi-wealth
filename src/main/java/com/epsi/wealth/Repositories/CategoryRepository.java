package com.epsi.wealth.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epsi.wealth.Models.CategoryModel;

public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {
}