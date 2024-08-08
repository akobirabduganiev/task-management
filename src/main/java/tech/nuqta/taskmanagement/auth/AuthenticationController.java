package tech.nuqta.taskmanagement.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.nuqta.taskmanagement.common.ResponseMessage;

/**
 * The AuthenticationController class handles the authentication-related APIs.
 * It provides methods for user registration, user authentication, account activation, and token refreshing.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication-related operations")
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Authenticate a user and get a token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Activate user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account activated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid token",
                    content = @Content)
    })
    @GetMapping("/activate-account")
    public ResponseEntity<ResponseMessage> confirm(
            @RequestParam String token
    ) throws MessagingException {
        return ResponseEntity.ok(service.activateAccount(token));
    }

    @Operation(summary = "Refresh the authentication token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content)
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request

    ) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }
        authorization = authorization.substring(7);
        return ResponseEntity.ok(service.refreshToken(authorization));
    }
}