package tech.nuqta.taskmanagement.task.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.taskmanagement.enums.TaskPriority;
import tech.nuqta.taskmanagement.enums.TaskStatus;

@Getter
@Setter
public class TaskUpdateRequest {
    @NotNull(message = "Id is required")
    private Long id;
    @NotNull(message = "Title is required")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    @NotNull(message = "Description is required")
    @NotBlank(message = "Description is required")
    @Size(min = 3, message = "Description must be at least 3 characters")
    private String description;
    @NotNull(message = "Status is required")
    private TaskStatus status;
    @NotNull(message = "Priority is required")
    private TaskPriority priority;
    @NotNull(message = "Assignee is required")
    private Long assigneeId;
    @NotNull(message = "Author is required")
    private Long authorId;

}
