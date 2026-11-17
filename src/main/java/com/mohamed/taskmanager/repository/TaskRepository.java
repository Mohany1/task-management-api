package com.mohamed.taskmanager.repository;

import com.mohamed.taskmanager.model.Task;
import com.mohamed.taskmanager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
           "AND (:status IS NULL OR t.status = :status) ORDER BY t.dueDate ASC")
    List<Task> findByProject(@Param("projectId") Long projectId, @Param("status") TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId " +
           "AND (:status IS NULL OR t.status = :status) ORDER BY t.dueDate ASC")
    List<Task> findByAssignee(@Param("userId") Long userId, @Param("status") TaskStatus status);
}
