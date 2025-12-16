package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends MongoRepository<Reaction,String> {
    // On recupere toutes les reactions d'un post
    List<Reaction> findAllByPostId(String postId);

    // Pour verifier si un utilisateur a deja reagi a un post
    Optional<Reaction> findByPostIdAndUserId(String postId, String userId);
}
