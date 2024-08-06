package tech.nuqta.taskmanagement.user.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import tech.nuqta.taskmanagement.enums.Gender;

/**
 * The UserUpdateRequest class represents a request to update a user's information.
 * It contains the following fields:
 * - id: The id of the user to update.
 * - firstname: The first name of the user.
 * - lastname: The last name of the user.
 * - gender: The gender of the user.
 *
 * This class is typically used within the UserController to update a user's information.
 */
@Getter
@Setter
public class UserUpdateRequest {
    @NotNull(message = "Id is required")
    private Long id;
    @NotNull(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstname;
    @NotNull(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastname;
    private Gender gender;

}
