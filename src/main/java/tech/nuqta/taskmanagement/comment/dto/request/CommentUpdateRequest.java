package tech.nuqta.taskmanagement.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentUpdateRequest {
    @NotNull(message = "ID is required")
    private Long id;
    @NotNull(message = "Content is required")
    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;
    @NotNull(message = "Task ID is required")
    private Long taskId;
    @NotNull(message = "Author ID is required")
    private Long authorId;
}
