package com.epsi.wealth.Services;

import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.CategoryModel;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Repositories.CategoryRepository;
import com.epsi.wealth.Repositories.UserRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public CategoryModel createCategory(CategoryModel category, Long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        category.setUser(user);
        return categoryRepository.save(category);
    }
}
