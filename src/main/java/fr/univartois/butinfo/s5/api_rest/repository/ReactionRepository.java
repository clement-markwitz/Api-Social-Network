package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Reaction entities in MongoDB.
 */
public interface ReactionRepository extends MongoRepository<Reaction,String> {
    /**
     * Retrieves all reactions associated with a specific post ID.
     *
     * @param postId the ID of the post
     * @return a list of Reaction objects related to the specified post
     */
    List<Reaction> findAllByPostId(String postId);

    /**
     * Finds a reaction by post ID and user ID.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     * @return an Optional containing the Reaction if found, or empty if not found
     */
    Optional<Reaction> findByPostIdAndUserId(String postId, String userId);

    /**
     * Finds a reaction by comment ID and user ID.
     *
     * @param commentId the ID of the comment
     * @param userId the ID of the user
     * @return the Optional containing the Reaction if found, or empty if not found
     */
    Optional<Reaction> findByCommentIdAndUserId(String commentId, String userId);

    /**
     * Deletes a reaction by post ID and user ID.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user
     */
    void deleteByPostIdAndUserId(String postId, String userId);
}
