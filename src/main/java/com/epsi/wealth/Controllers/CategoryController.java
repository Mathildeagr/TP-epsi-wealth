package com.epsi.wealth.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.epsi.wealth.Models.CategoryModel;
import com.epsi.wealth.Services.CategoryService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping
    public List<CategoryModel> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public CategoryModel getById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PostMapping
    public CategoryModel createCategory(@Valid @RequestBody CategoryModel category, @RequestParam Long userId) {
        if (!userId.equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return categoryService.createCategory(category, userId);
    }

    @PutMapping("/{id}")
    public CategoryModel update(@PathVariable Long id, @Valid @RequestBody CategoryModel category) {
        CategoryModel existing = categoryService.getById(id);
        if (!existing.getUser().getId().equals(getCurrentUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return categoryService.update(id, category);
    }
}
