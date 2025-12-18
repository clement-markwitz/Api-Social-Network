package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.recommendation.CommunityRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.recommendation.FriendRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.recommendation.PageRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.recommendation.PostRecommendationDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.*;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.RecommendationService;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final RecommendationService recommendationService;

    /**
     * Constructor for UserController.
     *
     * @param userService the user service
     * @param userMapper the user mapper
     */
    public UserController(UserService userService, UserMapper userMapper, RecommendationService recommendationService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.recommendationService = recommendationService;
    }

    /**
     * Get the authenticated user's private profile.
     *
     * @param authentication the authentication object containing user details
     * @return ResponseEntity with UserPrivateProfileDto
     */
    @GetMapping("/me")
    @Operation(summary = "Get authenticated user's private profile", description = "Retrieves private profile information of the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Private profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    public ResponseEntity<UserPrivateProfileDto> authenticatedUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(userMapper.toPrivateProfileDto(user));
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
    }

    /**
     * Get a user's public profile by ID.
     *
     * @param id the user's ID
     * @return ResponseEntity containing UserPublicProfileDto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a user's public profile by ID", description = "Retrieves the public profile information of a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserPublicProfileDto> getUser(@PathVariable String id) {
        User user = userService.getById(id);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.toPublicProfileDto(user));
    }

    /**
     * Get all users as summaries.
     *
     * @return List of UserSummaryDto
     */
    @GetMapping
    @Operation(summary = "List all users", description = "Retrieves a list of all users in summary format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users list retrieved successfully")
    })
    public List<UserSummaryDto> getAllUsers() {
        List<User> users =  userService.getAll();
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
    @Operation(summary = "Delete a user by ID", description = "Deletes a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights to delete this user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Update a user's profile", description = "Updates profile information of a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights to update this profile"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Update a user's preferences", description = "Updates the preferences of a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Preferences updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights to update these preferences"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Update a user's interests", description = "Updates the interests of a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interests updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights to update these interests"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
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
    @Operation(summary = "Search user profiles", description = "Searches for user profiles using a search query.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profiles retrieved successfully")
    })
    public List<UserSummaryDto> searchProfiles(@RequestParam("q") String query) {
        List<User> users = userService.searchUsers(query);

        return users.stream().map(userMapper::toSummaryDto).toList();
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<List<FriendRecommendationDto>> getUserRecommendations(@PathVariable String id) {
        // Vérifier si l'utilisateur existe (optionnel, mais recommandé)
        userService.getById(id);

        List<FriendRecommendationDto> recommendations = recommendationService.getFriendRecommendations(id);
        return ResponseEntity.ok(recommendations);
    }
    @GetMapping("/{id}/recommendations/communities")
    public ResponseEntity<List<CommunityRecommendationDto>> getCommunityRecommendations(@PathVariable String id) {
        userService.getById(id);
        return ResponseEntity.ok(recommendationService.getCommunityRecommendations(id));
    }

    @GetMapping("/{id}/recommendations/pages")
    public ResponseEntity<List<PageRecommendationDto>> getPageRecommendations(@PathVariable String id) {
        userService.getById(id);
        return ResponseEntity.ok(recommendationService.getPageRecommendations(id));
    }

    @GetMapping("/{id}/recommendations/posts")
    public ResponseEntity<List<PostRecommendationDto>> getPostRecommendations(@PathVariable String id) {
        userService.getById(id);
        return ResponseEntity.ok(recommendationService.getPostRecommendations(id));
    }
}