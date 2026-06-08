package com.epsi.wealth.Services;

import org.springframework.stereotype.Service;

import com.epsi.wealth.Models.CategoryModel;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Repositories.CategoryRepository;
import com.epsi.wealth.Repositories.UserRepository;
import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<CategoryModel> getAll() {
        return categoryRepository.findAll();
    }

    public List<CategoryModel> getAllByUser(Long userId) {
        return categoryRepository.findByUserId(userId);
    }

    public CategoryModel getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
    }

    public CategoryModel createCategory(CategoryModel category, Long userId) {
        UserModel user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public CategoryModel update(Long id, CategoryModel data) {
        CategoryModel category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        category.setNom(data.getNom());
        category.setPlafondMensuel(data.getPlafondMensuel());
        return categoryRepository.save(category);
    }
}
