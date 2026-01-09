package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Controller for managing user follow relationships.
 */
@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final UserMapper userMapper;

    /**
     * Constructor for FollowController.
     *
     * @param followService the follow service
     * @param userMapper the user mapper
     */
    public FollowController(FollowService followService, UserMapper userMapper) {
        this.followService = followService;
        this.userMapper = userMapper;
    }

    /**
     * Helper method to get the authenticated user's ID.
     *
     * @return Authenticated user's ID
     */
    @Operation(summary = "Get Authenticated User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversation created successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "User not authenticated")
    })
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié.");
    }

    /**
     * Follow a user.
     *
     * @param followingId ID of the user to follow
     * @return ResponseEntity with status CREATED
     */
    @Operation(summary = "Follow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User followed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Cannot follow oneself")
    })
    @PostMapping("/{followingId}")
    public ResponseEntity<Void> followUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.followUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Unfollow a user.
     *
     * @param followingId ID of the user to unfollow
     * @return ResponseEntity with status NO_CONTENT
     */
    @Operation(summary = "Unfollow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User unfollowed successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Cannot unfollow oneself"),
    })
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.unfollowUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get the list of users the authenticated user is following.
     *
     * @return List of UserSummaryDto
     */
    @Operation(summary = "Get Following Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of following users retrieved successfully")
    })
    @GetMapping("/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing() {
        String followerId = getAuthenticatedUserId();
        List<User> users = followService.getFollowing(followerId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).toList());
    }

    /**
     * Get the list of users following the authenticated user.
     *
     * @return List of UserSummaryDto
     */
    @Operation(summary = "Get Followers Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of followers retrieved successfully"),
    })
    @GetMapping("/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers() {
        String followingId = getAuthenticatedUserId();
        List<User> users = followService.getFollowers(followingId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).toList());
    }

    /**
     * Get the list of friends (mutual followers) of a user.
     *
     * @param userId ID of the user
     * @return List of UserSummaryDto
     */
    @Operation(summary = "Get Friends Users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of friends retrieved successfully"),
    })
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserSummaryDto>> getFriends(@PathVariable String userId) {
        List<User> friends = followService.getFriends(userId);
        return ResponseEntity.ok(friends.stream().map(userMapper::toSummaryDto).toList());
    }

    @Operation(summary = "Get Following Users for a given user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of following users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowingByUser(@PathVariable String userId) {
        List<User> users = followService.getFollowing(userId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).toList());
    }

    @Operation(summary = "Get Followers Users for a given user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of followers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowersByUser(@PathVariable String userId) {
        List<User> users = followService.getFollowers(userId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).toList());
    }
}

