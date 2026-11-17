package com.mohamed.taskmanager.service;

import com.mohamed.taskmanager.dto.Dtos.*;
import com.mohamed.taskmanager.exception.ResourceNotFoundException;
import com.mohamed.taskmanager.model.*;
import com.mohamed.taskmanager.repository.TaskRepository;
import com.mohamed.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final CurrentUserService currentUser;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       ProjectService projectService, CurrentUserService currentUser) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectService = projectService;
        this.currentUser = currentUser;
    }

    public TaskResponse create(Long projectId, TaskRequest req) {
        Project project = projectService.getAuthorized(projectId);
        Task t = new Task();
        t.setProject(project);
        apply(t, req);
        return TaskResponse.from(taskRepository.save(t));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findByProject(Long projectId, TaskStatus status) {
        projectService.getAuthorized(projectId);
        return taskRepository.findByProject(projectId, status).stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findMine(TaskStatus status) {
        return taskRepository.findByAssignee(currentUser.get().getId(), status)
                .stream().map(TaskResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return TaskResponse.from(getAuthorized(id));
    }

    public TaskResponse update(Long id, TaskRequest req) {
        Task t = getAuthorized(id);
        apply(t, req);
        return TaskResponse.from(t);
    }

    public TaskResponse updateStatus(Long id, StatusUpdateRequest req) {
        Task t = getAuthorized(id);
        t.setStatus(req.status());
        return TaskResponse.from(t);
    }

    public void delete(Long id) {
        taskRepository.delete(getAuthorized(id));
    }

    private Task getAuthorized(Long id) {
        Task t = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task " + id + " not found"));
        projectService.checkAccess(t.getProject());
        return t;
    }

    private void apply(Task t, TaskRequest req) {
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setDueDate(req.dueDate());
        if (req.status() != null) t.setStatus(req.status());
        if (req.assigneeId() != null) {
            User assignee = userRepository.findById(req.assigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee " + req.assigneeId() + " not found"));
            t.setAssignee(assignee);
        } else {
            t.setAssignee(null);
        }
    }
}
