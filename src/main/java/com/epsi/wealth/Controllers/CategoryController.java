package com.epsi.wealth.Controllers;

import org.springframework.web.bind.annotation.*;

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
        return categoryService.createCategory(category, userId);
    }

    @PutMapping("/{id}")
    public CategoryModel update(@PathVariable Long id, @Valid @RequestBody CategoryModel category) {
        return categoryService.update(id, category);
    }
}
