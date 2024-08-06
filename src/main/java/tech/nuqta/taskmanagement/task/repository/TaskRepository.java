package tech.nuqta.taskmanagement.task.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.nuqta.taskmanagement.task.entity.TaskEntity;
import tech.nuqta.taskmanagement.user.entity.User;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id AND t.isDeleted = false")
    Optional<TaskEntity> findById(Long id);

    @Transactional
    @Query("SELECT t FROM TaskEntity t WHERE t.author = :author AND t.isDeleted = false")
    Page<TaskEntity> findByAssigneeAndIsDeletedFalse(User assignee, Pageable pageable);
}