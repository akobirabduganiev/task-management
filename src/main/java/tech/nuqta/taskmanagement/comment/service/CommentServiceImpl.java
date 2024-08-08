package tech.nuqta.taskmanagement.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.nuqta.taskmanagement.comment.dto.CommentDto;
import tech.nuqta.taskmanagement.comment.dto.request.CommentCreateRequest;
import tech.nuqta.taskmanagement.comment.dto.request.CommentUpdateRequest;
import tech.nuqta.taskmanagement.comment.entity.CommentEntity;
import tech.nuqta.taskmanagement.comment.repository.CommentRepository;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.exception.ItemNotFoundException;
import tech.nuqta.taskmanagement.exception.OperationNotPermittedException;
import tech.nuqta.taskmanagement.mapper.CommentMapper;
import tech.nuqta.taskmanagement.task.repository.TaskRepository;
import tech.nuqta.taskmanagement.user.entity.User;
import tech.nuqta.taskmanagement.user.repository.UserRepository;

/**
 * The CommentServiceImpl class is an implementation of the CommentService interface.
 * It provides methods for adding, deleting, updating, and retrieving comments.
 * This class interacts with the TaskRepository, UserRepository, and CommentRepository to perform database operations.
 * The comments are cached using a cache manager.
 * Logging functionality is provided using the SLF4J logger.
 *
 * @see CommentService
 * @see TaskRepository
 * @see UserRepository
 * @see CommentRepository
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * This method adds a comment to a task.
     *
     * @param request        the comment creation request object
     * @param connectedUser  the authenticated user
     * @return a response message indicating the success of the operation
     * @throws ItemNotFoundException         if the user or task is not found
     * @throws OperationNotPermittedException if the user is not allowed to comment on the task
     */
    @Override
    @CacheEvict(value = "comments", allEntries = true)
    public ResponseMessage addComment(CommentCreateRequest request, Authentication connectedUser) {
        var author = userRepository.findById(request.getAuthorId()).orElseThrow(
                () -> new ItemNotFoundException("User not found"));
        var task = taskRepository.findById(request.getTaskId()).orElseThrow(
                () -> new ItemNotFoundException("Task not found"));
        var user = (User) connectedUser.getPrincipal();
        if (!user.getId().equals(author.getId())) {
            throw new OperationNotPermittedException("You are not allowed to comment on this task");
        }
        var comment = new CommentEntity();
        comment.setAuthor(author);
        comment.setTask(task);
        comment.setContent(request.getContent());
        commentRepository.save(comment);
        log.info("Comment added successfully by user {}", author.getId());
        return new ResponseMessage("Comment added successfully");
    }

    /**
     * Deletes a comment with the specified comment ID.
     *
     * @param commentId      the ID of the comment to be deleted
     * @param connectedUser  the authenticated user
     * @return a ResponseMessage object indicating the result of the deletion operation
     * @throws ItemNotFoundException          if the comment is not found in the comment repository
     * @throws OperationNotPermittedException if the authenticated user is not the author of the comment
     *                                        and is not allowed to delete the comment
     */
    @Override
    @CacheEvict(value = "comments", allEntries = true)
    public ResponseMessage deleteComment(Long commentId, Authentication connectedUser) {
        var comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ItemNotFoundException("Comment not found"));
        var user = (User) connectedUser.getPrincipal();
        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new OperationNotPermittedException("You are not allowed to delete this comment");
        }
        comment.setIsDeleted(true);
        commentRepository.save(comment);
        log.info("Comment deleted successfully by user {}", user.getId());
        return new ResponseMessage("Comment deleted successfully");
    }

    /**
     * Updates the content of a comment based on the provided request, if the requesting user is the author of the comment.
     *
     * @param request         The {@link CommentUpdateRequest} object containing the updated content and the ID of the comment to be updated.
     * @param connectedUser   The {@link Authentication} object representing the currently authenticated user.
     * @return A {@link ResponseMessage} object indicating the status of the update operation.
     * @throws ItemNotFoundException         If the comment with the specified ID is not found in the comment repository.
     * @throws OperationNotPermittedException If the requesting user is not the author of the comment and therefore not allowed to update it.
     *
     * @since 1.0.0
     */
    @Override
    @CacheEvict(value = "comments", allEntries = true)
    public ResponseMessage updateComment(CommentUpdateRequest request, Authentication connectedUser) {
        var comment = commentRepository.findById(request.getId()).orElseThrow(
                () -> new ItemNotFoundException("Comment not found"));
        var user = (User) connectedUser.getPrincipal();
        if (!user.getId().equals(comment.getAuthor().getId())) {
            throw new OperationNotPermittedException("You are not allowed to update this comment");
        }
        comment.setContent(request.getContent());
        commentRepository.save(comment);
        log.info("Comment updated successfully by user {}", user.getId());
        return new ResponseMessage("Comment updated successfully");
    }

    /**
     * Retrieves a comment based on the provided comment ID.
     *
     * @param commentId the ID of the comment to retrieve
     * @return a ResponseMessage object containing the retrieved comment and a success message
     * @throws ItemNotFoundException if the comment with the provided ID does not exist
     * @see ResponseMessage
     * @see ItemNotFoundException
     */
    @Override
    @Cacheable("comments")
    public ResponseMessage getComment(Long commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ItemNotFoundException("Comment not found"));
        log.info("Comment retrieved successfully with id {}", commentId);
        return new ResponseMessage(CommentMapper.toDto(comment), "Comment retrieved successfully");
    }


    /**
     * Retrieves all comments based on the given pagination parameters.
     *
     * @param page The page number of the comments to be retrieved. Must be greater than 0.
     * @param size The number of comments to be retrieved per page. Must be greater than 0.
     * @return A PageResponse object containing a list of CommentDto objects representing the retrieved comments,
     *         along with pagination information.
     * @see PageResponse
     * @see CommentDto
     * @since 1.0
     */
    @Override
    @Cacheable("comments")
    public PageResponse<CommentDto> getAllComments(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var comments = commentRepository.findAll(pageable);
        log.info("All comments retrieved successfully for page {} and size {}", page, size);
        return new PageResponse<>(
                CommentMapper.toDtoList(comments.getContent()),
                comments.getNumber() + 1,
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.isFirst(),
                comments.isLast()
        );
    }

    /**
     * Retrieves a page of comments associated with a specific task.
     *
     * @param taskId the ID of the task for which to retrieve comments
     * @param page   the page number to retrieve (1-based index)
     * @param size   the number of comments per page
     * @return a {@link PageResponse} containing the comments on the specified task
     */
    @Override
    @Cacheable("comments")
    public PageResponse<CommentDto> getCommentsByTask(Long taskId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var comments = commentRepository.findAllByTaskId(taskId, pageable);
        log.info("Comments retrieved successfully for task {} with page {} and size {}", taskId, page, size);
        return new PageResponse<>(
                CommentMapper.toDtoList(comments.getContent()),
                comments.getNumber() + 1,
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.isFirst(),
                comments.isLast()
        );
    }

    /**
     * Retrieves comments written by a specific author.
     *
     * @param authorId the ID of the author whose comments are to be retrieved
     * @param page the page number to retrieve (starting from 1)
     * @param size the number of comments to retrieve per page
     * @return a PageResponse containing the comments written by the author, along with pagination details
     */
    @Override
    @Cacheable("comments")
    public PageResponse<CommentDto> getCommentsByAuthor(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var comments = commentRepository.findAllByAuthorId(authorId, pageable);
        log.info("Comments retrieved successfully for author {} with page {} and size {}", authorId, page, size);
        return new PageResponse<>(
                CommentMapper.toDtoList(comments.getContent()),
                comments.getNumber() + 1,
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.isFirst(),
                comments.isLast()
        );
    }

    /**
     * Retrieves comments by task and author.
     *
     * @param taskId    the ID of the task
     * @param authorId  the ID of the author
     * @param page      the page number
     * @param size      the number of comments per page
     * @return a PageResponse object containing a list of CommentDto objects and pagination information
     */
    @Override
    @Cacheable("comments")
    public PageResponse<CommentDto> getCommentsByTaskAndAuthor(Long taskId, Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var comments = commentRepository.findAllByTaskIdAndAuthorId(taskId, authorId, pageable);
        log.info("Comments retrieved successfully for task {} and author {} with page {} and size {}", taskId, authorId, page, size);
        return new PageResponse<>(
                CommentMapper.toDtoList(comments.getContent()),
                comments.getNumber() + 1,
                comments.getSize(),
                comments.getTotalElements(),
                comments.getTotalPages(),
                comments.isFirst(),
                comments.isLast()
        );
    }
}
