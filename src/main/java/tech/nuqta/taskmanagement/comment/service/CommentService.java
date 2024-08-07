package tech.nuqta.taskmanagement.comment.service;

import org.springframework.security.core.Authentication;
import tech.nuqta.taskmanagement.comment.dto.CommentDto;
import tech.nuqta.taskmanagement.comment.dto.request.CommentCreateRequest;
import tech.nuqta.taskmanagement.comment.dto.request.CommentUpdateRequest;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;

public interface CommentService {
    ResponseMessage addComment(CommentCreateRequest request, Authentication connectedUser);
    ResponseMessage deleteComment(Long commentId, Authentication connectedUser);
    ResponseMessage updateComment(CommentUpdateRequest request, Authentication connectedUser);
    ResponseMessage getComment(Long commentId);
    PageResponse<CommentDto> getAllComments(int page, int size);
    PageResponse<CommentDto> getCommentsByTask(Long taskId, int page, int size);
    PageResponse<CommentDto> getCommentsByAuthor(Long authorId, int page, int size);
    PageResponse<CommentDto> getCommentsByTaskAndAuthor(Long taskId, Long authorId, int page, int size);

}
