package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import fr.univartois.butinfo.s5.api_rest.model.User; // Import de votre classe User
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    @Autowired
    private FollowService followService;

    // Nouvelle méthode pour récupérer l'ID MongoDB (ID unique) de l'utilisateur connecté
    // Ceci remplace principal.getName() pour obtenir l'ID.
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User userDetails = (User) authentication.getPrincipal();
            return userDetails.getId(); // Retourne l'ID MongoDB
        }
        // Devrait être impossible si SecurityConfiguration fonctionne.
        throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié.");
    }

    // --- C (Create) ---
    @PostMapping("/{followingId}")
    public ResponseEntity<Void> followUser(@PathVariable String followingId, Principal principal) {
        // followerId est l'ID MongoDB de l'utilisateur connecté
        String followerId = getAuthenticatedUserId();

        // followingId est le username de l'URL
        followService.followUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // --- R (Read) ---
    @GetMapping("/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing(Principal principal) {
        String followerId = getAuthenticatedUserId();
        List<UserSummaryDto> following = followService.getFollowing(followerId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers(Principal principal) {
        String followingId = getAuthenticatedUserId();
        List<UserSummaryDto> followers = followService.getFollowers(followingId);
        return ResponseEntity.ok(followers);
    }

    // --- D (Delete) ---
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId, Principal principal) {
        String followerId = getAuthenticatedUserId();
        followService.unfollowUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}