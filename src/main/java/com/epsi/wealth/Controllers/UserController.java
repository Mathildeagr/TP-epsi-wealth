package com.epsi.wealth.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.epsi.wealth.Exceptions.EmailAlreadyExistsException;
import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Services.UserService;
import com.epsi.wealth.Services.UserService.Advisor;
import com.epsi.wealth.Services.UserService.DashboardDTO;
import com.epsi.wealth.Services.UserService.SafetyBufferDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserModel user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public UserModel getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // DTO pour le dashboard
    @GetMapping("/{id}/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getDashboard(id));
    }

    @GetMapping("/{id}/safety-buffer")
    public ResponseEntity<SafetyBufferDTO> getSafetyBuffer(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getSafetyBuffer(id));
    }

    @GetMapping("/{id}/advisor")
    public ResponseEntity<Advisor> getAdvisor(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getAdvisor(id));
    }
}
