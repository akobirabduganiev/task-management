package tech.nuqta.taskmanagement.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Task Management", description = "Endpoints for managing tasks")
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "Add a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task added successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> addTask(
            @RequestBody @Valid TaskCreateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.addTask(request, authentication));
    }

    @Operation(summary = "Get task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseMessage> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @Operation(summary = "Get tasks by priority")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @GetMapping("/get-task-by-priority/{priority}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByPriority(
            @PathVariable TaskPriority priority,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByPriority(priority, page, size, authentication));
    }

    @Operation(summary = "Get tasks by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @GetMapping("/get-task-by-status/{status}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByStatus(
            @PathVariable TaskStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByStatus(status, page, size, authentication));
    }

    @Operation(summary = "Get tasks by assignee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @GetMapping("/get-task-by-assignee/{assigneeId}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByAssignee(
            @PathVariable Long assigneeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId, page, size, authentication));
    }

    @Operation(summary = "Get tasks by author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @GetMapping("/get-task-by-author/{authorId}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksByAuthor(authorId, page, size, authentication));
    }

    @Operation(summary = "Get all tasks with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)) })
    })
    @GetMapping("/get-all")
    public ResponseEntity<PageResponse<TaskDto>> getTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(taskService.getTasks(page, size));
    }

    @Operation(summary = "Update a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateTask(
            @RequestBody @Valid TaskUpdateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTask(request, authentication));
    }

    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(taskService.deleteTask(id, authentication));
    }
}
