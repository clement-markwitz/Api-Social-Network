package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Follow;
import fr.univartois.butinfo.s5.api_rest.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends MongoRepository<Follow, String> {

    // On cherche maintenant avec des objets User
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    List<Follow> findAllByFollower(User follower);

    List<Follow> findAllByFollowing(User following);

    long deleteByFollowerAndFollowing(User follower, User following);
}