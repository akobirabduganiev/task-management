package tech.nuqta.taskmanagement.task.dto;

import tech.nuqta.taskmanagement.enums.TaskPriority;
import tech.nuqta.taskmanagement.enums.TaskStatus;
import tech.nuqta.taskmanagement.task.entity.TaskEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link TaskEntity}
 */
public record TaskDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long modifiedBy,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long authorId,
        Long assigneeId
) implements Serializable {
}