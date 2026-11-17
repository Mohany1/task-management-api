package com.mohamed.taskmanager.service;

import com.mohamed.taskmanager.dto.Dtos.*;
import com.mohamed.taskmanager.exception.ResourceNotFoundException;
import com.mohamed.taskmanager.model.User;
import com.mohamed.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUser;

    public UserService(UserRepository userRepository, CurrentUserService currentUser) {
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(load(id));
    }

    @Transactional(readOnly = true)
    public UserResponse me() {
        return UserResponse.from(currentUser.get());
    }

    public UserResponse updateRole(Long id, RoleUpdateRequest req) {
        User u = load(id);
        u.setRole(req.role());
        return UserResponse.from(u);
    }

    public void delete(Long id) {
        userRepository.delete(load(id));
    }

    private User load(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
    }
}
