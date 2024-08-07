package tech.nuqta.taskmanagement.task.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.exception.ItemNotFoundException;
import tech.nuqta.taskmanagement.exception.OperationNotPermittedException;
import tech.nuqta.taskmanagement.mapper.TaskMapper;
import tech.nuqta.taskmanagement.task.dto.TaskDto;
import tech.nuqta.taskmanagement.task.dto.request.TaskCreateRequest;
import tech.nuqta.taskmanagement.task.dto.request.TaskUpdateRequest;
import tech.nuqta.taskmanagement.task.repository.TaskRepository;
import tech.nuqta.taskmanagement.user.entity.User;
import tech.nuqta.taskmanagement.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseMessage addTask(TaskCreateRequest request, Authentication connectedUser) {
        var assignee = userRepository.findById(request.getAssigneeId()).orElseThrow(
                () -> new ItemNotFoundException("Assignee not found"));
        var author = userRepository.findById(request.getAuthorId()).orElseThrow(
                () -> new ItemNotFoundException("Author not found"));
        var user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(request.getAuthorId()))
            throw new OperationNotPermittedException("You are not authorized to create a task for another user");

        var task = TaskMapper.toEntity(request, assignee, author);

        taskRepository.save(task);
        log.info("Task with id: {} created", task.getId());
        return new ResponseMessage("Task created successfully");
    }

    @Override
    @Transactional
    public ResponseMessage updateTask(TaskUpdateRequest request, Authentication connectedUser) {
        var task = taskRepository.findById(request.getId()).orElseThrow(
                () -> new ItemNotFoundException("Task not found"));
        var user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(task.getAuthor().getId()))
            throw new OperationNotPermittedException("You are not authorized to update this task");

        TaskMapper.toEntity(request, task);

        taskRepository.save(task);
        log.info("Task with id: {} updated", task.getId());
        return new ResponseMessage("Task updated successfully");
    }

    @Override
    public ResponseMessage deleteTask(Long id, Authentication connectedUser) {
        var task = taskRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task not found"));
        var user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(task.getAuthor().getId()))
            throw new OperationNotPermittedException("You are not authorized to delete this task");

        task.setIsDeleted(true);
        taskRepository.save(task);
        log.info("Task with id: {} deleted", task.getId());
        return new ResponseMessage("Task deleted successfully");
    }

    @Override
    public ResponseMessage getTask(Long id, Authentication connectedUser) {
        var task = taskRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task not found"));
        var user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(task.getAuthor().getId()) && !user.getId().equals(task.getAssignee().getId()))
            throw new OperationNotPermittedException("You are not authorized to view this task");

        var taskDto = TaskMapper.toDto(task);
        log.info("Task with id: {} retrieved", task.getId());
        return new ResponseMessage(taskDto,"Task retrieved successfully");
    }

    @Override
    public PageResponse<TaskDto> getTasksByAssignee(Long assigneeId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var user = (User) connectedUser.getPrincipal();
        var assignee = userRepository.findById(assigneeId).orElseThrow(
                () -> new ItemNotFoundException("Assignee not found"));
        if (!user.getId().equals(assigneeId))
            throw new OperationNotPermittedException("You are not authorized to view tasks for another user");
        var tasks = taskRepository.findByAssigneeAndIsDeletedFalse(assignee, pageable);
        log.info("All tasks for assignee with id: {} retrieved", assigneeId);
        return new PageResponse<>(
                TaskMapper.toDtoList(tasks.getContent()),
                tasks.getNumber() + 1,
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages(),
                tasks.isFirst(),
                tasks.isLast()
        );
    }

    @Override
    public PageResponse<TaskDto> getTasksByAuthor(Long authorId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var user = (User) connectedUser.getPrincipal();
        var author = userRepository.findById(authorId).orElseThrow(
                () -> new ItemNotFoundException("Author not found"));
        if (!user.getId().equals(authorId))
            throw new OperationNotPermittedException("You are not authorized to view tasks for another user");
        var tasks = taskRepository.findByAuthorAndIsDeletedFalse(author, pageable);
        log.info("All tasks for author with id: {} retrieved", authorId);
        return new PageResponse<>(
                TaskMapper.toDtoList(tasks.getContent()),
                tasks.getNumber() + 1,
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages(),
                tasks.isFirst(),
                tasks.isLast()
        );
    }

    @Override
    public PageResponse<TaskDto> getTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var tasks = taskRepository.findByIsDeletedFalse(pageable);
        log.info("All tasks retrieved with page number: {} and size: {}", page, size);
        return new PageResponse<>(
                TaskMapper.toDtoList(tasks.getContent()),
                tasks.getNumber() + 1,
                tasks.getSize(),
                tasks.getTotalElements(),
                tasks.getTotalPages(),
                tasks.isFirst(),
                tasks.isLast()
        );
    }
}
