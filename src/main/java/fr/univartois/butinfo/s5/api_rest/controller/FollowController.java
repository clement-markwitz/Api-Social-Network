package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import fr.univartois.butinfo.s5.api_rest.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    /**
     * Récupère l'ID MongoDB de l'utilisateur connecté.
     */
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User userDetails) {
            return userDetails.getId();
        }

        throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié.");
    }

    /**
     * Create a follow relationship.
     *
     * @param followingId the ID of the user to follow
     * @return ResponseEntity with status CREATED
     */
    @PostMapping("/{followingId}")
    @Operation(summary = "Suivre un utilisateur", description = "Permet à l'utilisateur authentifié de suivre un autre utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur suivi avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur à suivre non trouvé")
    })
    public ResponseEntity<Void> followUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.followUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Get the list of users the authenticated user is following.
     *
     * @return ResponseEntity with list of UserSummaryDto
     */
    @GetMapping("/following")
    @Operation(summary = "Lister les utilisateurs suivis", description = "Récupère une liste des utilisateurs que l'utilisateur authentifié suit.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs suivis récupérée avec succès")
    })
    public ResponseEntity<List<UserSummaryDto>> getFollowing() {
        String followerId = getAuthenticatedUserId();
        List<UserSummaryDto> following = followService.getFollowing(followerId);
        return ResponseEntity.ok(following);
    }

    /**
     * Get the list of followers of the authenticated user.
     *
     * @return ResponseEntity with list of UserSummaryDto
     */
    @GetMapping("/followers")
    @Operation(summary = "Lister les abonnés", description = "Récupère une liste des utilisateurs qui suivent l'utilisateur authentifié.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des abonnés récupérée avec succès")
    })
    public ResponseEntity<List<UserSummaryDto>> getFollowers() {
        String followingId = getAuthenticatedUserId();
        List<UserSummaryDto> followers = followService.getFollowers(followingId);
        return ResponseEntity.ok(followers);
    }

    /**
     * Unfollow a user.
     *
     * @param followingId the ID of the user to unfollow
     * @return ResponseEntity with status NO_CONTENT
     */
    @DeleteMapping("/{followingId}")
    @Operation(summary = "Ne plus suivre un utilisateur", description = "Permet à l'utilisateur authentifié de ne plus suivre un autre utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur non suivi avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur à ne plus suivre non trouvé")
    })
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId) {
        String followerId = getAuthenticatedUserId();
        followService.unfollowUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}