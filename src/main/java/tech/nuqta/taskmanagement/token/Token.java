package tech.nuqta.taskmanagement.token;

import jakarta.persistence.*;
import lombok.*;
import tech.nuqta.taskmanagement.user.entity.User;


import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;
}
