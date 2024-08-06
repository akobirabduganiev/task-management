package tech.nuqta.taskmanagement.mapper;

import tech.nuqta.taskmanagement.task.dto.TaskDto;
import tech.nuqta.taskmanagement.task.dto.request.TaskCreateRequest;
import tech.nuqta.taskmanagement.task.dto.request.TaskUpdateRequest;
import tech.nuqta.taskmanagement.task.entity.TaskEntity;
import tech.nuqta.taskmanagement.user.entity.User;

import java.util.List;

public class TaskMapper {
    public static TaskDto toDto(TaskEntity entity) {
        return new TaskDto(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedBy(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getAuthor().getId(),
                entity.getAssignee().getId()
        );
    }

    public static TaskEntity toEntity(TaskCreateRequest request, User author, User assignee) {
        TaskEntity entity = new TaskEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setPriority(request.getPriority());
        entity.setAuthor(author);
        entity.setAssignee(assignee);
        return entity;
    }

    public static TaskEntity toEntity(TaskUpdateRequest request, TaskEntity entity) {
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setPriority(request.getPriority());
        return entity;
    }

    public static List<TaskDto> toDtoList(List<TaskEntity> content) {
        return content.stream().map(TaskMapper::toDto).toList();
    }
}
