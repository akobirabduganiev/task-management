package tech.nuqta.taskmanagement.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Update user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateUser(
            @RequestBody @Valid UserUpdateRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(userService.updateUser(request, connectedUser));
    }

    @Operation(summary = "Update user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @PutMapping("/update-password")
    public ResponseEntity<ResponseMessage> updatePassword(
            @RequestBody @Valid UserPasswordUpdateRequest request,
            Authentication connectedUser) {
        return ResponseEntity.ok(userService.updatePassword(request, connectedUser));
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteUser(
            @RequestParam Long id,
            Authentication connectedUser) {
        return ResponseEntity.ok(userService.deleteUser(id, connectedUser));
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/get")
    public ResponseEntity<ResponseMessage> getUser(
            @RequestParam Long id,
            Authentication connectedUser) {
        return ResponseEntity.ok(userService.getUser(id, connectedUser));
    }

    @Operation(summary = "Get all users with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)) })
    })
    @GetMapping("/get-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageResponse<UserDto>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getUsers(page, size));
    }
}
