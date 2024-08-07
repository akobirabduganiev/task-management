package tech.nuqta.taskmanagement.mapper;

import tech.nuqta.taskmanagement.comment.dto.CommentDto;
import tech.nuqta.taskmanagement.comment.entity.CommentEntity;

import java.util.List;

public class CommentMapper {
    public static CommentDto toDto(CommentEntity entity) {
        return new CommentDto(
                entity.getId(),
                entity.getIsDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedBy(),
                entity.getContent(),
                entity.getTask().getId(),
                entity.getAuthor().getId()
        );
    }
    public static List<CommentDto> toDtoList(List<CommentEntity> content) {
        return content.stream().map(CommentMapper::toDto).toList();
    }
}
