package com.mohamed.taskmanager.service;

import com.mohamed.taskmanager.dto.Dtos.*;
import com.mohamed.taskmanager.exception.ResourceNotFoundException;
import com.mohamed.taskmanager.model.Project;
import com.mohamed.taskmanager.model.User;
import com.mohamed.taskmanager.repository.ProjectRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CurrentUserService currentUser;

    public ProjectService(ProjectRepository projectRepository, CurrentUserService currentUser) {
        this.projectRepository = projectRepository;
        this.currentUser = currentUser;
    }

    public ProjectResponse create(ProjectRequest req) {
        Project p = new Project();
        p.setName(req.name());
        p.setDescription(req.description());
        p.setOwner(currentUser.get());
        return ProjectResponse.from(projectRepository.save(p));
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        User me = currentUser.get();
        List<Project> projects = currentUser.isAdmin()
                ? projectRepository.findAll() : projectRepository.findByOwner(me);
        return projects.stream().map(ProjectResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return ProjectResponse.from(getAuthorized(id));
    }

    public ProjectResponse update(Long id, ProjectRequest req) {
        Project p = getAuthorized(id);
        p.setName(req.name());
        p.setDescription(req.description());
        return ProjectResponse.from(p);
    }

    public void delete(Long id) {
        projectRepository.delete(getAuthorized(id));
    }

    /** Loads a project and ensures the current user is its owner or an admin. */
    public Project getAuthorized(Long id) {
        Project p = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project " + id + " not found"));
        checkAccess(p);
        return p;
    }

    public void checkAccess(Project p) {
        User me = currentUser.get();
        if (me.getRole() != com.mohamed.taskmanager.model.Role.ADMIN
                && !p.getOwner().getId().equals(me.getId())) {
            throw new AccessDeniedException("Not your project");
        }
    }
}
