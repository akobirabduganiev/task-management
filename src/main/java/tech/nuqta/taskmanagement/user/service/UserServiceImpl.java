package tech.nuqta.taskmanagement.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.enums.RoleName;
import tech.nuqta.taskmanagement.exception.AppBadRequestException;
import tech.nuqta.taskmanagement.exception.OperationNotPermittedException;
import tech.nuqta.taskmanagement.mapper.UserMapper;
import tech.nuqta.taskmanagement.user.dto.UserDto;
import tech.nuqta.taskmanagement.user.dto.request.UserPasswordUpdateRequest;
import tech.nuqta.taskmanagement.user.dto.request.UserUpdateRequest;
import tech.nuqta.taskmanagement.user.entity.User;
import tech.nuqta.taskmanagement.user.repository.UserRepository;

import java.util.Optional;

/***
 * Implementation of the UserService interface that provides methods for interacting with user entities.
 * It is responsible for updating, deleting, retrieving, and managing user data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ResponseMessage updateUser(UserUpdateRequest request, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var userToUpdate = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));
        if (!user.getId().equals(userToUpdate.getId()) &&
                user.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ADMIN))) {
            throw new OperationNotPermittedException("You are not authorized to update this user");
        }
        userToUpdate.setFirstname(request.getFirstname());
        userToUpdate.setLastname(request.getLastname());
        Optional.ofNullable(request.getGender()).ifPresent(userToUpdate::setGender);
        userRepository.save(userToUpdate);
        log.info("User with id: {} updated", request.getId());
        return new ResponseMessage("User updated successfully");
    }

    /**
     * Deletes a user with the given ID.
     *
     * @param id              the ID of the user to be deleted
     * @param connectedUser   the authenticated user performing the operation
     * @return a ResponseMessage indicating the success of the operation
     * @throws OperationNotPermittedException if the connected user is not authorized to delete the user
     */
    @Override
    public ResponseMessage deleteUser(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var foundUser = getById(id);
        if (!user.getId().equals(foundUser.getId()) &&
                user.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ADMIN))) {
            throw new OperationNotPermittedException("You are not authorized to delete this user");
        }
        foundUser.setDeleted(true);
        foundUser.setEnabled(false);
        userRepository.save(foundUser);
        log.info("User with id: {} deleted", id);
        return new ResponseMessage("User deleted successfully");
    }

    /**
     * This method retrieves a user by their ID.
     *
     * @param id             The ID of the user to retrieve.
     * @param connectedUser  The currently authenticated user.
     * @return A ResponseMessage object containing the retrieved user as a DTO and a success message.
     * @throws OperationNotPermittedException  If the authenticated user is not authorized to retrieve the user.
     */
    @Override
    public ResponseMessage getUser(Long id, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var retrievedUser = getById(id);
        if (!user.getId().equals(retrievedUser.getId()) &&
                user.getRoles().stream().noneMatch(role -> role.getName().equals(RoleName.ADMIN))) {
            throw new OperationNotPermittedException("You are not authorized to retrieve this user");
        }
        return new ResponseMessage(UserMapper.toDto(retrievedUser), "User retrieved successfully");
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param page The page number (starting from 1) to retrieve.
     * @param size The number of users to retrieve per page.
     * @return A PageResponse containing the list of UserDto objects, as well as pagination information.
     */
    @Override
    public PageResponse<UserDto> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<User> users = userRepository.findAll(pageable);
        return new PageResponse<>(
                UserMapper.toDtoList(users.getContent()),
                users.getNumber() + 1,
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );

    }


    /**
     * Updates the password of a user.
     *
     * @param request         the UserPasswordUpdateRequest containing the old and new password
     * @param connectedUser   the current authenticated user
     * @return a ResponseMessage indicating whether the password was updated successfully
     * @throws AppBadRequestException       if the user is not found
     * @throws OperationNotPermittedException  if the authenticated user is not authorized to update the password
     */
    @Override
    public ResponseMessage updatePassword(UserPasswordUpdateRequest request, Authentication connectedUser) {
        var user = ((User) connectedUser.getPrincipal());
        var currentUser = userRepository.findById(request.getId()).orElseThrow(() -> new AppBadRequestException("User not found"));
        if (!user.getId().equals(currentUser.getId())) {
            throw new OperationNotPermittedException("You are not authorized to update this user's password");
        }

        authenticateAndUpdateUserPassword(request.getOldPassword(), request.getNewPassword(), currentUser);
        log.info("User with id: {} password updated", request.getId());
        return new ResponseMessage("Password updated successfully");
    }

    /**
     * Authenticates the user with the old password and updates the user's password to the new password.
     *
     * @param oldPassword the old password of the user
     * @param newPassword the new password to update
     * @param user the user whose password needs to be updated
     */
    private void authenticateAndUpdateUserPassword(String oldPassword, String newPassword, User user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        oldPassword
                )
        );
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    /**
     * Retrieves a User object by its ID.
     *
     * @param id the ID of the User to retrieve
     * @return the User object identified by the given ID
     * @throws AppBadRequestException if the User with the given ID does not exist
     */
    private User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppBadRequestException("User not found"));
    }
}
