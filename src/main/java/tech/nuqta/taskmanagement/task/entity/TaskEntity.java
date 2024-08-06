package tech.nuqta.taskmanagement.task.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.taskmanagement.common.BaseEntity;
import tech.nuqta.taskmanagement.enums.TaskPriority;
import tech.nuqta.taskmanagement.enums.TaskStatus;
import tech.nuqta.taskmanagement.user.entity.User;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class TaskEntity extends BaseEntity {
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Enumerated(EnumType.STRING)
  private TaskStatus status;
  private TaskPriority priority;

  @ManyToOne
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToOne
  @JoinColumn(name = "assignee_id")
  private User assignee;
}