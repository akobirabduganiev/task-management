package tech.nuqta.taskmanagement.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tech.nuqta.taskmanagement.common.PageResponse;
import tech.nuqta.taskmanagement.common.ResponseMessage;
import tech.nuqta.taskmanagement.user.dto.UserDto;
import tech.nuqta.taskmanagement.user.dto.request.UserPasswordUpdateRequest;
import tech.nuqta.taskmanagement.user.dto.request.UserUpdateRequest;
import tech.nuqta.taskmanagement.user.service.UserService;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateUser(@RequestBody @Valid UserUpdateRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(userService.updateUser(request, connectedUser));
    }

    @PutMapping("/update-password")
    public ResponseEntity<ResponseMessage> updatePassword(@RequestBody @Valid UserPasswordUpdateRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(userService.updatePassword(request, connectedUser));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteUser(@RequestParam Long id, Authentication connectedUser) {
        return ResponseEntity.ok(userService.deleteUser(id, connectedUser));
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseMessage> getUser(@RequestParam Long id, Authentication connectedUser) {
        return ResponseEntity.ok(userService.getUser(id, connectedUser));
    }

    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserDto>> getUsers(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getUsers(page, size));
    }
}
