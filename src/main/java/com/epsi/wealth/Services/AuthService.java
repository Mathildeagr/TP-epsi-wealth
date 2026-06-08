package com.epsi.wealth.Services;

import com.epsi.wealth.Models.UserModel;
import com.epsi.wealth.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {

    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token, Long userId, String email, LocalDateTime expires) {}

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private long expiration;

    public AuthService(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public UserModel register(UserModel user) {
        return userService.createUser(user);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect");
        }

        UserModel user = userService.getUserByEmail(request.email());
        String token = jwtUtil.generateToken(user.getId());
        LocalDateTime expires = LocalDateTime.now().plusSeconds(expiration / 1000);

        return new LoginResponse(token, user.getId(), user.getEmail(), expires);
    }
}

