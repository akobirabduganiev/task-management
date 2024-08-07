package tech.nuqta.taskmanagement.task.service;

import org.springframework.security.core.Authentication;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.enums.TaskPriority;
import tech.nuqta.taskmanagement.enums.TaskStatus;
import tech.nuqta.taskmanagement.task.dto.TaskDto;
import tech.nuqta.taskmanagement.task.dto.request.TaskCreateRequest;
import tech.nuqta.taskmanagement.task.dto.request.TaskUpdateRequest;

public interface TaskService {
    ResponseMessage addTask(TaskCreateRequest request, Authentication connectedUser);

    ResponseMessage updateTask(TaskUpdateRequest request, Authentication connectedUser);

    ResponseMessage deleteTask(Long id, Authentication connectedUser);

    ResponseMessage getTask(Long id);

    PageResponse<TaskDto> getTasksByPriority(TaskPriority priority, int page, int size, Authentication connectedUser);

    PageResponse<TaskDto> getTasksByStatus(TaskStatus status, int page, int size, Authentication connectedUser);

    PageResponse<TaskDto> getTasksByAssignee(Long assigneeId, int page, int size, Authentication connectedUser);

    PageResponse<TaskDto> getTasksByAuthor(Long authorId, int page, int size, Authentication connectedUser);

    PageResponse<TaskDto> getTasks(int page, int size);
}
