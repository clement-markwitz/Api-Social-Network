package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Follow;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Follow relationships between users.
 */
@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {

    // On cherche maintenant avec des objets User
    /**
     * Find a follow relationship by follower and following users.
     *
     * @param follower  the user who is following
     * @param following the user being followed
     * @return an Optional containing the Follow relationship if found, otherwise empty
     */
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    /**
     * Find all follow relationships where the specified user is the follower.
     *
     * @param follower the user who is following others
     * @return a list of Follow relationships
     */
    List<Follow> findAllByFollower(User follower);

    /**
     * Find all follow relationships where the specified user is being followed.
     *
     * @param following the user who is being followed
     * @return a list of Follow relationships
     */
    List<Follow> findAllByFollowing(User following);

    /**
     * Delete a follow relationship by follower and following users.
     *
     * @param follower  the user who is following
     * @param following the user being followed
     * @return the number of deleted follow relationships
     */
    long deleteByFollowerAndFollowing(User follower, User following);
}