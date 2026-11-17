package com.mohamed.taskmanager.controller;

import com.mohamed.taskmanager.dto.Dtos.*;
import com.mohamed.taskmanager.model.TaskStatus;
import com.mohamed.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> create(@PathVariable Long projectId, @Valid @RequestBody TaskRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(projectId, req));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> byProject(@PathVariable Long projectId,
                                        @RequestParam(required = false) TaskStatus status) {
        return taskService.findByProject(projectId, status);
    }

    @GetMapping("/tasks/my")
    public List<TaskResponse> mine(@RequestParam(required = false) TaskStatus status) {
        return taskService.findMine(status);
    }

    @GetMapping("/tasks/{id}")
    public TaskResponse one(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PutMapping("/tasks/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskRequest req) {
        return taskService.update(id, req);
    }

    @PatchMapping("/tasks/{id}/status")
    public TaskResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        return taskService.updateStatus(id, req);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
