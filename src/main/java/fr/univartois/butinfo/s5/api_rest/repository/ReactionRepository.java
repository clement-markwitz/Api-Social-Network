package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Reaction entities in MongoDB.
 */
public interface ReactionRepository extends MongoRepository<Reaction,String> {
    // On recupere toutes les reactions d'un post
    /**
     * Retrieves all reactions associated with a specific post ID.
     *
     * @param postId the ID of the post
     * @return a list of Reaction objects related to the specified post
     */
    List<Reaction> findAllByPostId(String postId);

    // Pour verifier si un utilisateur a deja reagi a un post
    /**
     * Finds a reaction by post ID and user ID.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @return an Optional containing the Reaction if found, or empty if not found
     */
    Optional<Reaction> findByPostIdAndUserId(String postId, String userId);

    Optional<Reaction> findByCommentIdAndUserId(String commentId, String userId);

    void deleteByPostIdAndUserId(String postId, String userId);
}
