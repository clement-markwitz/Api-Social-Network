package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository interface for managing Comment entities in MongoDB.
 */
public interface CommentRepository extends MongoRepository<Comment, String> {

    /**
     * Find all comments associated with a specific post ID.
     *
     * @param postId the ID of the post
     * @return a list of comments for the specified post
     */
    List<Comment> findAllByPostId(String postId);
}
