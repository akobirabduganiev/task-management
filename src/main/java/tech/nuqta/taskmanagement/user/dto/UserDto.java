package tech.nuqta.taskmanagement.user.dto;


import tech.nuqta.taskmanagement.enums.Gender;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public record UserDto(
        Long id,
        String firstname,
        String lastname,
        String email,
        Gender gender,
        boolean accountLocked,
        boolean enabled,
        List<Object> authorities,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long modifiedBy) implements Serializable {
}