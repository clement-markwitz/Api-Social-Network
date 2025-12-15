package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    private String getCurrentUserId() {
        return "current-user-id-123";
    }

    // --- C (Create) ---
    /**
     * Follows another user.
     * POST /api/follows/{followingId}
     */
    @PostMapping("/{followingId}")
    public ResponseEntity<Void> followUser(@PathVariable String followingId) {
        String followerId = getCurrentUserId();
        followService.followUser(followerId, followingId);
        // Utilisation de 201 Created après une création réussie
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // --- R (Read) ---
    /**
     * Retrieves the list of users the connected user is following.
     * GET /api/follows/following
     */
    @GetMapping("/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing() {
        String followerId = getCurrentUserId();
        List<UserSummaryDto> following = followService.getFollowing(followerId);
        return ResponseEntity.ok(following);
    }

    /**
     * Retrieves the list of users who are following the connected user.
     * GET /api/follows/followers
     */
    @GetMapping("/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers() {
        String followingId = getCurrentUserId();
        List<UserSummaryDto> followers = followService.getFollowers(followingId);
        return ResponseEntity.ok(followers);
    }

    // --- D (Delete) ---
    /**
     * Stops following a user (Unfollow).
     * DELETE /api/follows/{followingId}
     */
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId) {
        String followerId = getCurrentUserId();
        followService.unfollowUser(followerId, followingId);
        // Utilisation de 204 No Content après une suppression réussie
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}