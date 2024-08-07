package tech.nuqta.taskmanagement.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
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

    @Override
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

    @Override
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

    @Override
    public ResponseMessage getComment(Long commentId) {
        var comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ItemNotFoundException("Comment not found"));
        log.info("Comment retrieved successfully with id {}", commentId);
        return new ResponseMessage(CommentMapper.toDto(comment), "Comment retrieved successfully");
    }


    @Override
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

    @Override
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

    @Override
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

    @Override
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
