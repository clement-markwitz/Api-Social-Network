package fr.univartois.butinfo.s5.api_rest.auth;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserLoginDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPrivateProfileDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling authentication-related endpoints.
 * @author Cornet Benjamin
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserMapper userMapper) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    /**
     * Register a new user.
     *
     * @param dto UserCreateDto containing user registration information
     * @return UserPrivateProfileDto of the newly created user
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public UserPrivateProfileDto register(@Valid @RequestBody UserCreateDto dto) {
        User registeredUser = authenticationService.register(dto);
        return userMapper.toPrivateProfileDto(registeredUser);
    }

    /**
     * Authenticate a user and return a JWT token.
     *
     * @param dto UserLoginDto containing user login information
     * @return Map containing the JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate a user", description = "Allows a user to log in and receive a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> authenticate(@RequestBody UserLoginDto dto) {
        User authenticatedUser = authenticationService.authenticate(dto);
        String token = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.ok(Map.of("token", token));
    }
}