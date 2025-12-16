package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserPrivateProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserPublicProfileDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for user-related endpoints.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Constructor for UserController.
     *
     * @param userService the user service
     * @param userMapper the user mapper
     */
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Get the authenticated user's private profile.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with UserPrivateProfileDto
     */
    @GetMapping("/me")
    public ResponseEntity<UserPrivateProfileDto> authenticatedUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userMapper.toPrivateProfileDto(user));
    }

    /**
     * Get a user's public profile by ID.
     *
     * @param id the user's ID
     * @return UserPublicProfileDto
     */
    @GetMapping("/{id}")
    public UserPublicProfileDto getUser(@PathVariable String id) {
        User user = userService.getById(id);
        return userMapper.toPublicProfileDto(user);
    }

    /**
     * Get all users as summaries.
     *
     * @return List of UserSummaryDto
     */
    @GetMapping
    public List<UserSummaryDto> getAllUsers() {
        List<User> users =  userService.getAll();
        return users.stream()
                .map(userMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user's ID
     * @return Boolean indicating success
     */
    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable String id) {
        return userService.delete(id);
    }
}