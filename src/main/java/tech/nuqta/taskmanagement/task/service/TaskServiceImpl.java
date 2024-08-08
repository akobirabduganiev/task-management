package tech.nuqta.taskmanagement.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.enums.TaskPriority;
import tech.nuqta.taskmanagement.enums.TaskStatus;
import tech.nuqta.taskmanagement.exception.ItemNotFoundException;
import tech.nuqta.taskmanagement.exception.OperationNotPermittedException;
import tech.nuqta.taskmanagement.mapper.TaskMapper;
import tech.nuqta.taskmanagement.task.dto.TaskDto;
import tech.nuqta.taskmanagement.task.dto.request.TaskCreateRequest;
import tech.nuqta.taskmanagement.task.dto.request.TaskUpdateRequest;
import tech.nuqta.taskmanagement.task.repository.TaskRepository;
import tech.nuqta.taskmanagement.user.entity.User;
import tech.nuqta.taskmanagement.user.repository.UserRepository;


/**
 * This class is an implementation of the TaskService interface.
 * It provides the functionality to perform various operations on tasks, such as adding, updating, deleting, and retrieving tasks.
 * The class is annotated with @Service, indicating that it is a Spring service component.
 * It also has a constructor annotated with @RequiredArgsConstructor, which is an annotation from the Lombok library that generates a constructor with the required dependencies.
 * The class also uses the @Slf4j annotation from the Lombok library to automatically generate a logger variable.
 * The class implements the TaskService interface and provides implementations for all its methods.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    /**
     * Adds a new task to the system.
     *
     * @param request        the task creation request object
     * @param connectedUser  the authenticated user
     * @return a response message indicating the success of the operation
     * @throws ItemNotFoundException         if the assignee or author is not found in the user repository
     * @throws OperationNotPermittedException if the authenticated user is not authorized to create a task for another user
     */
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public ResponseMessage addTask(TaskCreateRequest request, Authentication connectedUser) {
        var assignee = userRepository.findById(request.getAssigneeId()).orElseThrow(
                () -> new ItemNotFoundException("Assignee not found"));
        var author = userRepository.findById(request.getAuthorId()).orElseThrow(
                () -> new ItemNotFoundException("Author not found"));
        var user = (User) connectedUser.getPrincipal();

        if (!user.getId().equals(request.getAuthorId()))
            throw new OperationNotPermittedException("You are not authorized to create a task for another user");
        if (request.getAssigneeId().equals(request.getAuthorId()))
            throw new OperationNotPermittedException("Assignee and author cannot be the same");

        var task = TaskMapper.toEntity(request, assignee, author);

        taskRepository.save(task);
        log.info("Task with id: {} created", task.getId());
        return new ResponseMessage("Task created successfully");
    }

    /**
     * Updates a task with the provided information.
     *
     * @param request        The TaskUpdateRequest object containing the updated task information.
     * @param connectedUser  The authentication object representing the currently connected user.
     * @return A ResponseMessage object indicating the result of the update operation.
     * @throws ItemNotFoundException           If the task with the provided ID is not found.
     * @throws OperationNotPermittedException   If the currently connected user is not authorized to update the task.
     */
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
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

    /**
     * Deletes a task.
     *
     * @param id The ID of the task to be deleted.
     * @param connectedUser The authenticated user performing the delete operation.
     * @return A {@link ResponseMessage} indicating the result of the delete operation.
     * @throws ItemNotFoundException If the task with the provided ID is not found.
     * @throws OperationNotPermittedException If the authenticated user is not authorized to delete the task.
     */
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
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

    /**
     * Retrieves a task by its ID.
     *
     * @param id The ID of the task to retrieve.
     * @return A ResponseMessage object containing the task DTO and a success message.
     * @throws ItemNotFoundException if the task with the specified ID is not found.
     *
     * @since <unspecified>
     */
    @Override
    @Cacheable("tasks")
    public ResponseMessage getTask(Long id) {
        var task = taskRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("Task not found"));
        var taskDto = TaskMapper.toDto(task);
        log.info("Task with id: {} retrieved", task.getId());
        return new ResponseMessage(taskDto, "Task retrieved successfully");
    }

    /**
     * Retrieves tasks with the specified priority.
     *
     * @param priority       the priority of the tasks to retrieve
     * @param page           the page number of the results to retrieve
     * @param size           the maximum number of results per page
     * @param connectedUser  the authenticated user making the request
     * @return a PageResponse containing the tasks with the specified priority, including pagination information
     */
    @Override
    @Cacheable("tasks")
    public PageResponse<TaskDto> getTasksByPriority(TaskPriority priority, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var user = (User) connectedUser.getPrincipal();
        var tasks = taskRepository.findByPriorityAndIsDeletedFalse(priority, pageable);
        log.info("All tasks for priority: {} retrieved", priority);
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

    /**
     * Retrieves a page of tasks with a given status.
     *
     * @param status The status of the tasks to retrieve.
     * @param page The page number to retrieve.
     * @param size The number of tasks per page.
     * @param connectedUser The authenticated user making the request.
     * @return A PageResponse object containing the retrieved tasks and pagination information.
     */
    @Override
    @Cacheable("tasks")
    public PageResponse<TaskDto> getTasksByStatus(TaskStatus status, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var user = (User) connectedUser.getPrincipal();
        var tasks = taskRepository.findByStatusAndIsDeletedFalse(status, pageable);
        log.info("All tasks for status: {} retrieved", status);
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

    /**
     * Retrieves a page of tasks assigned to a specific assignee.
     *
     * @param assigneeId The ID of the assignee.
     * @param page The page number to retrieve (starting from 1).
     * @param size The number of tasks to retrieve per page.
     * @param connectedUser The authenticated user performing the operation.
     * @return A PageResponse object containing a list of TaskDto objects, along with pagination information.
     * @throws ItemNotFoundException If the assignee with the specified ID is not found.
     * @throws OperationNotPermittedException If the connected user is not authorized to view tasks for another user.
     */
    @Override
    @Cacheable("tasks")
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

    /**
     * Retrieves a page of tasks created by a specific author.
     *
     * @param authorId         the ID of the author whose tasks are to be retrieved
     * @param page             the page number of the results to be retrieved
     * @param size             the number of tasks per page
     * @param connectedUser    the authentication details of the currently connected user
     * @return a {@link PageResponse} object containing the list of tasks, as well as pagination details
     *
     * @throws ItemNotFoundException        if the specified author is not found
     * @throws OperationNotPermittedException if the currently connected user is not authorized to view tasks for another user
     */
    @Override
    @Cacheable("tasks")
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

    /**
     * Retrieves a page of tasks based on the given page number and page size.
     *
     * @param page The page number (1-based) to retrieve.
     * @param size The number of tasks to retrieve per page.
     * @return A PageResponse object containing the list of TaskDto objects for the requested page,
     *         as well as additional information about the page such as total elements and total pages.
     */
    @Override
    @Cacheable("tasks")
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
