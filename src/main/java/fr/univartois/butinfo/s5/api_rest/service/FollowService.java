package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Follow;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.FollowRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository; // Import du UserRepository (à créer)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;

    private UserSummaryDto mapToUserSummaryDto(String userId) {
        return new UserSummaryDto(
                userId,
                "Pseudo-" + userId.substring(0, Math.min(userId.length(), 5)),
                "avatar/url/" + userId.substring(0, Math.min(userId.length(), 5))
        );
    }


    /**
     * Crée une relation de suivi (Follow).
     * @param followerId The ID of the user performing the follow.
     * @param followingId The ID of the target user.
     */
    public void followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            // Empêche un utilisateur de se suivre lui-même
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
     * @param followerId The ID of the user stopping the follow.
     * @param followingId The ID of the target user.
     */
    @Transactional
    public void unfollowUser(String followerId, String followingId) {
        long deletedCount = followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        if (deletedCount == 0) {
            // La relation de suivi n'existait pas
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow relationship not found.");
        }
    }

    /**
     * Récupère la liste des personnes suivies par l'utilisateur (Following).
     * @param followerId The ID of the requesting user.
     * @return List of UserSummaryDto of the followed users.
     */
    public List<UserSummaryDto> getFollowing(String followerId) {
        List<Follow> follows = followRepository.findAllByFollowerId(followerId);
        List<String> followingIds = follows.stream()
                .map(f -> f.getFollowing().getId())
                .collect(Collectors.toList());

        return followingIds.stream()
                .map(this::mapToUserSummaryDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère la liste des utilisateurs qui suivent l'utilisateur (Followers).
     * @param followingId The ID of the requesting user.
     * @return List of UserSummaryDto of the followers.
     */
    public List<UserSummaryDto> getFollowers(String followingId) {
        List<Follow> follows = followRepository.findAllByFollowingId(followingId);
        List<String> followerIds = follows.stream()
                .map(f -> f.getFollower().getId())
                .collect(Collectors.toList());

        return followerIds.stream()
                .map(this::mapToUserSummaryDto)
                .collect(Collectors.toList());
    }
}
