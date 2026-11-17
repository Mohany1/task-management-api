package com.mohamed.taskmanager.service;

import com.mohamed.taskmanager.dto.Dtos.*;
import com.mohamed.taskmanager.exception.DuplicateResourceException;
import com.mohamed.taskmanager.model.Role;
import com.mohamed.taskmanager.model.User;
import com.mohamed.taskmanager.repository.UserRepository;
import com.mohamed.taskmanager.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder,
                       AuthenticationManager authManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username()))
            throw new DuplicateResourceException("Username already taken");
        if (userRepository.existsByEmail(req.email()))
            throw new DuplicateResourceException("Email already registered");
        User saved = userRepository.save(
                new User(req.username(), req.email(), encoder.encode(req.password()), Role.USER));
        return UserResponse.from(saved);
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User user = userRepository.findByUsername(req.username()).orElseThrow();
        return new AuthResponse(jwtService.generateToken(user.getUsername()), "Bearer",
                user.getUsername(), user.getRole());
    }
}
