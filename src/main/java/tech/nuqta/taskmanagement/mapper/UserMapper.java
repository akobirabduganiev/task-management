package tech.nuqta.taskmanagement.mapper;



import tech.nuqta.taskmanagement.user.dto.UserDto;
import tech.nuqta.taskmanagement.user.entity.User;

import java.util.Arrays;
import java.util.List;

/**
 * The UserMapper class is responsible for mapping User objects to UserDto objects and vice versa.
 */
public class UserMapper {
    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getFirstname(), user.getLastname(),
                user.getEmail(),user.getGender(), user.isAccountLocked(), user.isEnabled(),
                Arrays.asList(user.getAuthorities().toArray()), user.getCreatedAt(), user.getUpdatedAt(), user.getModifiedBy());
    }

    public static List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(UserMapper::toDto).toList();
    }
}
