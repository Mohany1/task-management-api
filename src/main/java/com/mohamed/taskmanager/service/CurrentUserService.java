package com.mohamed.taskmanager.service;

import com.mohamed.taskmanager.exception.ResourceNotFoundException;
import com.mohamed.taskmanager.model.Role;
import com.mohamed.taskmanager.model.User;
import com.mohamed.taskmanager.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User get() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public boolean isAdmin() {
        return get().getRole() == Role.ADMIN;
    }
}
