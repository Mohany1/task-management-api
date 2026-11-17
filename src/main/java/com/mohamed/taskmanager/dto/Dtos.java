package com.mohamed.taskmanager.dto;

import com.mohamed.taskmanager.model.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Dtos {
    private Dtos() {}

    // ----- auth -----
    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Email @Size(max = 100) String email,
            @NotBlank @Size(min = 6, max = 100) String password) {}

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    public record AuthResponse(String token, String tokenType, String username, Role role) {}

    // ----- users -----
    public record UserResponse(Long id, String username, String email, Role role) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
        }
    }

    public record RoleUpdateRequest(@NotNull Role role) {}

    // ----- projects -----
    public record ProjectRequest(
            @NotBlank @Size(max = 100) String name,
            @Size(max = 500) String description) {}

    public record ProjectResponse(Long id, String name, String description, String owner, LocalDateTime createdAt) {
        public static ProjectResponse from(Project p) {
            return new ProjectResponse(p.getId(), p.getName(), p.getDescription(),
                    p.getOwner().getUsername(), p.getCreatedAt());
        }
    }

    // ----- tasks -----
    public record TaskRequest(
            @NotBlank @Size(max = 150) String title,
            @Size(max = 1000) String description,
            TaskStatus status,
            LocalDate dueDate,
            Long assigneeId) {}

    public record StatusUpdateRequest(@NotNull TaskStatus status) {}

    public record TaskResponse(Long id, String title, String description, TaskStatus status,
                               LocalDate dueDate, Long projectId, String assignee) {
        public static TaskResponse from(Task t) {
            return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(),
                    t.getDueDate(), t.getProject().getId(),
                    t.getAssignee() == null ? null : t.getAssignee().getUsername());
        }
    }
}
