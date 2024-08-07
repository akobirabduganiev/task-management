package tech.nuqta.taskmanagement.task.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.enums.TaskPriority;
import tech.nuqta.taskmanagement.enums.TaskStatus;
import tech.nuqta.taskmanagement.task.dto.TaskDto;
import tech.nuqta.taskmanagement.task.dto.request.TaskCreateRequest;
import tech.nuqta.taskmanagement.task.dto.request.TaskUpdateRequest;
import tech.nuqta.taskmanagement.task.service.TaskService;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> addTask(@RequestBody @Valid TaskCreateRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(taskService.addTask(request, authentication));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseMessage> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @GetMapping("/get-task-by-priority/{priority}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByPriority(@PathVariable TaskPriority priority,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "20") int size,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByPriority(priority, page, size, authentication));
    }

    @GetMapping("/get-task-by-status/{status}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByStatus(@PathVariable TaskStatus status,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status, page, size, authentication));
    }

    @GetMapping("/get-task-by-assignee/{assigneeId}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByAssignee(@PathVariable Long assigneeId,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "20") int size,
                                                                    Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId, page, size, authentication));
    }

    @GetMapping("/get-task-by-author/{authorId}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByAuthor(@PathVariable Long authorId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByAuthor(authorId, page, size, authentication));
    }

    @GetMapping("/get-all")
    public ResponseEntity<PageResponse<TaskDto>> getTasks(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(taskService.getTasks(page, size));
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateTask(@RequestBody @Valid TaskUpdateRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTask(request, authentication));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteTask(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(taskService.deleteTask(id, authentication));
    }


}
