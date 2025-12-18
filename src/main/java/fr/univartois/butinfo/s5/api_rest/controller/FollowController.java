package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
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

    public FollowController(FollowService followService, UserMapper userMapper) {
        this.followService = followService;
        this.userMapper = userMapper;
    }

    /**
     * Helper method to get the authenticated user's ID.
     * @return Authenticated user's ID
     */
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié.");
    }

    /**
     * Follow a user.
     * @param followingId ID of the user to follow
     * @return ResponseEntity with status CREATED
     */
    @PostMapping("/{followingId}")
    public ResponseEntity<Void> followUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.followUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Unfollow a user.
     * @param followingId ID of the user to unfollow
     * @return ResponseEntity with status NO_CONTENT
     */
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.unfollowUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get the list of users the authenticated user is following.
     * @return List of UserSummaryDto
     */
    @GetMapping("/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing() {
        String followerId = getAuthenticatedUserId();
        List<User> users = followService.getFollowing(followerId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).toList());
    }

    /**
     * Get the list of users following the authenticated user.
     * @return List of UserSummaryDto
     */
    @GetMapping("/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers() {
        String followingId = getAuthenticatedUserId();
        List<User> users = followService.getFollowers(followingId);
        return ResponseEntity.ok(users.stream().map(userMapper::toSummaryDto).toList());
    }

    /**
     * Get the list of friends (mutual followers) of a user.
     * @param userId ID of the user
     * @return List of UserSummaryDto
     */
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserSummaryDto>> getFriends(@PathVariable String userId) {
        List<User> friends = followService.getFriends(userId);
        return ResponseEntity.ok(friends.stream().map(userMapper::toSummaryDto).toList());
    }
}