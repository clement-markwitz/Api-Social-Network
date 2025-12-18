package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Follow;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.FollowRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Service class for managing follow relationships between users.
 */
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    /**
     * Allows a user to follow another user.
     *
     * @param followerId  the ID of the user who wants to follow
     * @param followingId the ID of the user to be followed
     */
    public void followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "On ne peut pas se suivre soi-même.");
        }

        // On récupère les VRAIS objets User car Follow utilise @DBRef
        User follower = getUserById(followerId);
        User following = getUserById(followingId);

        if (followRepository.findByFollowerAndFollowing(follower, following).isPresent()) {
            return; // Déjà suivi
        }

        Follow newFollow = new Follow(null, follower, following, LocalDateTime.now());
        followRepository.save(newFollow);
    }

    /**
     * Allows a user to unfollow another user.
     *
     * @param followerId  the ID of the user who wants to unfollow
     * @param followingId the ID of the user to be unfollowed
     */
    @Transactional
    public void unfollowUser(String followerId, String followingId) {
        User follower = getUserById(followerId);
        User following = getUserById(followingId);

        long deletedCount = followRepository.deleteByFollowerAndFollowing(follower, following);
        if (deletedCount == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Relation de suivi introuvable.");
        }
    }

    // Retourne des User (pas de DTO)
    /**
     * Retrieves the list of users that a specific user is following.
     *
     * @param followerId the ID of the user whose followings are to be retrieved
     * @return a list of users being followed by the specified user
     */
    public List<User> getFollowing(String followerId) {
        User follower = getUserById(followerId);
        return followRepository.findAllByFollower(follower).stream()
                .map(Follow::getFollowing)
                .toList();
    }

    // Retourne des User (pas de DTO)
    /**
     * Retrieves the list of users who are following a specific user.
     *
     * @param followingId the ID of the user whose followers are to be retrieved
     * @return a list of users following the specified user
     */
    public List<User> getFollowers(String followingId) {
        User following = getUserById(followingId);
        return followRepository.findAllByFollowing(following).stream()
                .map(Follow::getFollower)
                .toList();
    }

    // Retourne des User (Amis)
    /**
     * Retrieves the list of mutual friends for a specific user.
     *
     * @param userId the ID of the user whose friends are to be retrieved
     * @return a list of users who are mutual friends with the specified user
     */
    public List<User> getFriends(String userId) {
        User currentUser = getUserById(userId);

        // 1. Qui est-ce que je suis ?
        List<User> myFollowings = followRepository.findAllByFollower(currentUser).stream()
                .map(Follow::getFollowing)
                .toList();

        // 2. Parmi eux, qui me suit en retour ?
        return myFollowings.stream()
                .filter(targetUser -> followRepository.findByFollowerAndFollowing(targetUser, currentUser).isPresent())
                .toList();
    }

    /**
     * Helper method to retrieve a User by ID, throwing an exception if not found.
     *
     * @param id the ID of the user
     * @return the User object
     */
    private User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable : " + id));
    }
}