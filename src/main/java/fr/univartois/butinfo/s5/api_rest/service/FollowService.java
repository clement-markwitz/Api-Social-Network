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

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private UserRepository userRepository;

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
    public List<User> getFollowing(String followerId) {
        User follower = getUserById(followerId);
        return followRepository.findAllByFollower(follower).stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());
    }

    // Retourne des User (pas de DTO)
    public List<User> getFollowers(String followingId) {
        User following = getUserById(followingId);
        return followRepository.findAllByFollowing(following).stream()
                .map(Follow::getFollower)
                .collect(Collectors.toList());
    }

    // Retourne des User (Amis)
    public List<User> getFriends(String userId) {
        User currentUser = getUserById(userId);

        // 1. Qui est-ce que je suis ?
        List<User> myFollowings = followRepository.findAllByFollower(currentUser).stream()
                .map(Follow::getFollowing)
                .toList();

        // 2. Parmi eux, qui me suit en retour ?
        return myFollowings.stream()
                .filter(targetUser -> followRepository.findByFollowerAndFollowing(targetUser, currentUser).isPresent())
                .collect(Collectors.toList());
    }

    private User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable : " + id));
    }
}