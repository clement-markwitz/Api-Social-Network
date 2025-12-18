package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing Post entities in MongoDB.
 */
@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    /**
     * Find all posts authored by a specific user with pagination.
     *
     * @param authorId the ID of the author
     * @param pageable pagination information
     * @return a page of posts authored by the specified user
     */
    Page<Post> findAllByAuthorId(String authorId, Pageable pageable);
    /**
     * Find all posts in a specific community with pagination.
     *
     * @param communityId the ID of the community
     * @param pageable pagination information
     * @return a page of posts in the specified community
     */
    Page<Post> findAllByCommunityId(String communityId, Pageable pageable);
    /**
     * Find all posts on a specific page with pagination.
     *
     * @param pageId the ID of the page
     * @param pageable pagination information
     * @return a page of posts on the specified page
     */
    Page<Post> findAllByPageId(String pageId, Pageable pageable);
    /**
     * Find all posts containing specific text (case insensitive).
     *
     * @param text the text to search for
     * @return a list of posts containing the specified text
     */
    List<Post> findAllByTextContainingIgnoreCase(String text);

    /**
     * Count the number of posts created between the specified start and end dates.
     *
     * @param start the start date
     * @param end the end date
     * @return the count of posts created in the specified date range
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
