package tech.nuqta.taskmanagement.comment.controller;

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
import tech.nuqta.taskmanagement.comment.dto.CommentDto;
import tech.nuqta.taskmanagement.comment.dto.request.CommentCreateRequest;
import tech.nuqta.taskmanagement.comment.dto.request.CommentUpdateRequest;
import tech.nuqta.taskmanagement.comment.service.CommentService;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comment-related operations")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Add a new comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) })
    })
    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> addComment(@RequestBody @Valid CommentCreateRequest request,
                                                      Authentication connectedUser) {
        return ResponseEntity.ok(commentService.addComment(request, connectedUser));
    }

    @Operation(summary = "Delete a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) })
    })
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<ResponseMessage> deleteComment(@RequestParam Long commentId,
                                                         Authentication connectedUser) {
        return ResponseEntity.ok(commentService.deleteComment(commentId, connectedUser));
    }

    @Operation(summary = "Update a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) })
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateComment(@RequestBody @Valid CommentUpdateRequest request,
                                                         Authentication connectedUser) {
        return ResponseEntity.ok(commentService.updateComment(request, connectedUser));
    }

    @Operation(summary = "Get a specific comment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) })
    })
    @GetMapping("/{commentId}")
    public ResponseEntity<ResponseMessage> getComment(@RequestParam Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    @Operation(summary = "Get all comments with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)) })
    })
    @GetMapping("/all")
    public ResponseEntity<PageResponse<CommentDto>> getAllComments(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(commentService.getAllComments(page, size));
    }

    @Operation(summary = "Get all comments for a specific task with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)) })
    })
    @GetMapping("/by-task/{taskId}")
    public ResponseEntity<PageResponse<CommentDto>> getCommentsByTask(@RequestParam Long taskId,
                                                                      @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId, page, size));
    }

    @Operation(summary = "Get all comments by a specific author with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)) })
    })
    @GetMapping("/by-author/{authorId}")
    public ResponseEntity<PageResponse<CommentDto>> getCommentsByAuthor(@RequestParam Long authorId,
                                                                        @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(commentService.getCommentsByAuthor(authorId, page, size));
    }

    @Operation(summary = "Get comments by task and author with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class)) })
    })
    @GetMapping("/by-task-and-author")
    public ResponseEntity<PageResponse<CommentDto>> getCommentsByTaskAndAuthor(@RequestParam Long taskId,
                                                                               @RequestParam Long authorId,
                                                                               @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(commentService.getCommentsByTaskAndAuthor(taskId, authorId, page, size));
    }
}
