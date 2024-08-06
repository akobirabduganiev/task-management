package tech.nuqta.taskmanagement.user.service;

import org.springframework.security.core.Authentication;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.user.dto.UserDto;
import tech.nuqta.taskmanagement.user.dto.request.UserPasswordUpdateRequest;
import tech.nuqta.taskmanagement.user.dto.request.UserUpdateRequest;


public interface UserService {
    ResponseMessage updateUser(UserUpdateRequest user, Authentication connectedUser);
    ResponseMessage deleteUser(Long id, Authentication connectedUser);
    ResponseMessage getUser(Long id, Authentication connectedUser);
    PageResponse<UserDto> getUsers(int page, int size);
    ResponseMessage updatePassword(UserPasswordUpdateRequest request, Authentication connectedUser);
}
