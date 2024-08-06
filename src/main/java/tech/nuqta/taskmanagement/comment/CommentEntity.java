package tech.nuqta.taskmanagement.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.taskmanagement.common.BaseEntity;
import tech.nuqta.taskmanagement.task.entity.TaskEntity;
import tech.nuqta.taskmanagement.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class CommentEntity extends BaseEntity {
    private String content;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}