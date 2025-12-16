package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Import essentiel pour l'authentification
import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    // Suppression de la méthode privée getCurrentUserId()

    // --- C (Create) ---
    /**
     * Follows another user.
     * POST /api/follows/{followingId}
     * Requiert un utilisateur authentifié.
     */
    @PostMapping("/{followingId}")
    public ResponseEntity<Void> followUser(@PathVariable String followingId, Principal principal) {
        // L'ID de l'utilisateur connecté est récupéré via principal.getName()
        String followerId = principal.getName();
        followService.followUser(followerId, followingId);
        // Utilisation de 201 Created après une création réussie
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // --- R (Read) ---
    /**
     * Retrieves the list of users the connected user is following.
     * GET /api/follows/following
     * Requiert un utilisateur authentifié.
     */
    @GetMapping("/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing(Principal principal) {
        // L'ID de l'utilisateur connecté est récupéré via principal.getName()
        String followerId = principal.getName();
        List<UserSummaryDto> following = followService.getFollowing(followerId);
        return ResponseEntity.ok(following);
    }

    /**
     * Retrieves the list of users who are following the connected user.
     * GET /api/follows/followers
     * Requiert un utilisateur authentifié.
     */
    @GetMapping("/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers(Principal principal) {
        // L'ID de l'utilisateur connecté est récupéré via principal.getName()
        String followingId = principal.getName();
        List<UserSummaryDto> followers = followService.getFollowers(followingId);
        return ResponseEntity.ok(followers);
    }

    // --- D (Delete) ---
    /**
     * Stops following a user (Unfollow).
     * DELETE /api/follows/{followingId}
     * Requiert un utilisateur authentifié.
     */
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId, Principal principal) {
        // L'ID de l'utilisateur connecté est récupéré via principal.getName()
        String followerId = principal.getName();
        followService.unfollowUser(followerId, followingId);
        // Utilisation de 204 No Content après une suppression réussie
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}