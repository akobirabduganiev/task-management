package tech.nuqta.taskmanagement.comment.dto;

import tech.nuqta.taskmanagement.comment.entity.CommentEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link CommentEntity}
 */
public record CommentDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long createdBy,
        Long modifiedBy,
        String content,
        Long taskId,
        Long authorId
) implements Serializable {
}