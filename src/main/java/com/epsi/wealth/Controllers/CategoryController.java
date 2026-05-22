package com.epsi.wealth.Controllers;

import org.springframework.web.bind.annotation.*;

import com.epsi.wealth.Models.CategoryModel;
import com.epsi.wealth.Services.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping
    public CategoryModel createCategory(@Valid @RequestBody CategoryModel category, @RequestParam Long userId) {
        return categoryService.createCategory(category, userId);
    }
}
