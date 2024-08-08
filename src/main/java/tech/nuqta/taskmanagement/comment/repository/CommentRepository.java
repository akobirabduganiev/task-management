package tech.nuqta.taskmanagement.comment.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.nuqta.taskmanagement.comment.entity.CommentEntity;
import tech.nuqta.taskmanagement.task.entity.TaskEntity;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {


    @Override
    @Transactional
    @Query("SELECT c FROM CommentEntity c WHERE c.isDeleted = false")
    Page<CommentEntity> findAll(Pageable pageable);

    @Transactional
    @Query("SELECT c FROM CommentEntity c WHERE c.task.id = :taskId AND c.isDeleted = false")
    Page<CommentEntity> findAllByTaskId(Long taskId, Pageable pageable);

    @Transactional
    @Query("SELECT c FROM CommentEntity c WHERE c.author.id = :authorId AND c.isDeleted = false")
    Page<CommentEntity> findAllByAuthorId(Long authorId, Pageable pageable);

    @Transactional
    @Query("SELECT c FROM CommentEntity c WHERE c.task.id = :taskId AND c.author.id = :authorId AND c.isDeleted = false")
    Page<CommentEntity> findAllByTaskIdAndAuthorId(Long taskId, Long authorId, Pageable pageable);
}