package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.service.FollowService;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    // CORRECTION 1 : On remplace @Autowired sur le champ par 'final' + constructeur
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    /**
     * Récupère l'ID MongoDB de l'utilisateur connecté.
     */
    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // CORRECTION 2 : "Pattern Matching for instanceof" (plus besoin de faire le cast (User) manuellement)
        if (authentication != null && authentication.getPrincipal() instanceof User userDetails) {
            return userDetails.getId();
        }

        throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié.");
    }

    // --- C (Create) ---
    @PostMapping("/{followingId}")
    public ResponseEntity<Void> followUser(@PathVariable String followingId) {
        // CORRECTION 3 : J'ai retiré le paramètre "Principal principal" qui ne servait à rien
        String followerId = getAuthenticatedUserId();
        followService.followUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // --- R (Read) ---
    @GetMapping("/following")
    public ResponseEntity<List<UserSummaryDto>> getFollowing() {
        // CORRECTION 3 : Paramètre "Principal" supprimé ici aussi
        String followerId = getAuthenticatedUserId();
        List<UserSummaryDto> following = followService.getFollowing(followerId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/followers")
    public ResponseEntity<List<UserSummaryDto>> getFollowers() {
        // CORRECTION 3 : Paramètre "Principal" supprimé ici aussi
        String followingId = getAuthenticatedUserId();
        List<UserSummaryDto> followers = followService.getFollowers(followingId);
        return ResponseEntity.ok(followers);
    }

    // --- D (Delete) ---
    @DeleteMapping("/{followingId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable String followingId) {
        // CORRECTION 3 : Paramètre "Principal" supprimé ici aussi
        String followerId = getAuthenticatedUserId();
        followService.unfollowUser(followerId, followingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}