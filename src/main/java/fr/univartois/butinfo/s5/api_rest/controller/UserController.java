package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.*;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(userMapper.toPrivateProfileDto(user));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Non authentifié");
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
        // CORRECTION SONAR : Remplacement de .collect(Collectors.toList()) par .toList()
        return users.stream()
                .map(userMapper::toSummaryDto)
                .toList();
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user's ID
     * @return Boolean indicating success
     */
    @DeleteMapping("/{id}")
    public Boolean deleteUser(@PathVariable String id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        userService.checkUserRights(id, currentUser);

        return userService.delete(id);
    }

    /**
     * Update a user's profile.
     *
     * @param id the user's ID
     * @param updateDto the profile update data
     * @param authentication the authentication object
     * @return ResponseEntity with updated UserPublicProfileDto
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserPublicProfileDto> updateProfile(@PathVariable String id, @Valid @RequestBody ProfileUpdateDto updateDto, Authentication authentication) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = (User) authentication.getPrincipal();
        userService.checkUserRights(existingUser.getId(), currentUser);

        userMapper.updateProfileFromDto(updateDto, existingUser.getProfile());

        User updatedUser = userService.updateUser(existingUser);

        return ResponseEntity.ok(userMapper.toPublicProfileDto(updatedUser));
    }

    /**
     * Update a user's preferences.
     *
     * @param id the user's ID
     * @param updateDto the preferences update data
     * @param authentication the authentication object
     * @return ResponseEntity with updated PreferencesDto
     */
    @PutMapping("/{id}/preferences")
    public ResponseEntity<PreferencesDto> updatePreferences(@PathVariable String id, @RequestBody PreferencesUpdateDto updateDto, Authentication authentication) {
        User existingUser = userService.getById(id);
        if (existingUser == null) return ResponseEntity.notFound().build();

        User currentUser = (User) authentication.getPrincipal();
        userService.checkUserRights(existingUser.getId(), currentUser);

        userMapper.updatePreferencesFromDto(updateDto, existingUser.getPrefs());
        User updatedUser = userService.updateUser(existingUser);

        return ResponseEntity.ok(userMapper.toPreferencesDto(updatedUser.getPrefs()));
    }

    /**
     * Update a user's interests.
     *
     * @param id the user's ID
     * @param updateDto the interests update data
     * @param authentication the authentication object
     * @return ResponseEntity with updated InterestsDto
     */
    @PutMapping("/{id}/interests")
    public ResponseEntity<InterestsDto> updateInterests(@PathVariable String id, @RequestBody InterestsUpdateDto updateDto, Authentication authentication) {
        User existingUser = userService.getById(id);
        if (existingUser == null) return ResponseEntity.notFound().build();

        User currentUser = (User) authentication.getPrincipal();
        userService.checkUserRights(existingUser.getId(), currentUser);

        userMapper.updateInterestsFromDto(updateDto, existingUser.getInterests());
        User updatedUser = userService.updateUser(existingUser);

        return ResponseEntity.ok(userMapper.toInterestsDto(updatedUser.getInterests()));
    }


    /**
     * Search for user profiles by query.
     *
     * @param query the search query
     * @return List of UserSummaryDto matching the query
     */
    @GetMapping("/search")
    public List<UserSummaryDto> searchProfiles(@RequestParam("q") String query) {
        List<User> users = userService.searchUsers(query);

        return users.stream().map(userMapper::toSummaryDto).toList();
    }
}