package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.PostStats;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostStatsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing posts.
 */
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;

    /**
     * Constructor for PostService.
     *
     * @param postRepository the post repository
     * @param postStatsRepository the post stats repository
     */
    public PostService(PostRepository postRepository, PostStatsRepository postStatsRepository) {
        this.postRepository = postRepository;
        this.postStatsRepository = postStatsRepository;
    }

    /**
     * Create a new post.
     *
     * @param post the post entity
     * @param author the author of the post
     * @return the created post
     */
    public Post createPost(Post post,  User author) {

        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        PostStats stats = new PostStats(0, 0);
        stats = postStatsRepository.save(stats);
        post.setStats(stats);

        return postRepository.save(post);
    }

    /**
     * Get a post by its ID.
     *
     * @param id the post ID
     * @return the post
     */
    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable"));
    }

    /**
     * Get all posts.
     *
     * @return list of all posts
     */
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    /**
     * Search posts by keyword in text.
     *
     * @param keyword the search keyword
     * @return list of matching posts
     */
    public List<Post> searchPosts(String keyword) {
        return postRepository.findAllByTextContainingIgnoreCase(keyword);
    }

    /**
     * Update an existing post.
     *
     * @param post the post entity
     * @return the updated post
     */
    public Post updatePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    /**
     * Delete a post.
     *
     * @param post the post entity
     */
    public void deletePost(Post post) {
        if (post.getStats() != null && post.getStats().getId() != null) {
            postStatsRepository.deleteById(post.getStats().getId());
        }
        postRepository.delete(post);
    }

    /**
     * Get posts by community ID.
     *
     * @param communityId the community ID
     * @return the list of posts in the community
     */
    public List<Post> getPostsByCommunity(String communityId) {
        return postRepository.findByCommunityId(communityId);
    }

    /**
     * Get posts by page ID.
     *
     * @param pageId the page ID
     * @return the list of posts on the page
     */
    public List<Post> getPostsByPage(String pageId) {
        return postRepository.findByPageId(pageId);
    }
}