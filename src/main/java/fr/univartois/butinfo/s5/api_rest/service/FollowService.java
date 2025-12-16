package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Follow;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.FollowRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
// J'ai supprimé l'import "java.util.stream.Collectors" car on n'en a plus besoin avec .toList()

@Service
@RequiredArgsConstructor // CORRECTION 1 : Génère le constructeur pour l'injection
public class FollowService {

    // CORRECTION 1 : On remplace @Autowired par private final
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private UserSummaryDto mapToUserSummaryDto(String userId) {
        return new UserSummaryDto(
                userId,
                "Pseudo-" + userId.substring(0, Math.min(userId.length(), 5)),
                "avatar/url/" + userId.substring(0, Math.min(userId.length(), 5))
        );
    }

    /**
     * Crée une relation de suivi (Follow).
     */
    public void followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself.");
        }

        if (followRepository.findByFollowerIdAndFollowingId(followerId, followingId).isPresent()) {
            return;
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower introuvable"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur à suivre introuvable"));

        Follow newFollow = new Follow(
                null,
                follower,
                following,
                LocalDateTime.now()
        );
        followRepository.save(newFollow);
    }

    /**
     * Supprime une relation de suivi (Unfollow).
     */
    @Transactional
    public void unfollowUser(String followerId, String followingId) {
        long deletedCount = followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        if (deletedCount == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow relationship not found.");
        }
    }

    /**
     * Récupère la liste des personnes suivies par l'utilisateur (Following).
     */
    public List<UserSummaryDto> getFollowing(String followerId) {
        List<Follow> follows = followRepository.findAllByFollowerId(followerId);

        // CORRECTION 2 : Optimisation du stream et utilisation de .toList()
        return follows.stream()
                .map(f -> f.getFollowing().getId())
                .map(this::mapToUserSummaryDto)
                .toList(); // <--- Ici, c'est la version moderne que Sonar attend
    }

    /**
     * Récupère la liste des utilisateurs qui suivent l'utilisateur (Followers).
     */
    public List<UserSummaryDto> getFollowers(String followingId) {
        List<Follow> follows = followRepository.findAllByFollowingId(followingId);

        // CORRECTION 2 : Optimisation du stream et utilisation de .toList()
        return follows.stream()
                .map(f -> f.getFollower().getId())
                .map(this::mapToUserSummaryDto)
                .toList(); // <--- Ici aussi
    }
}