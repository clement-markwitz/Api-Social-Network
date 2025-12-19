package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Comment;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.PostStats;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.CommentRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostStatsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing comments.
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          PostStatsRepository postStatsRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.postStatsRepository = postStatsRepository;
    }

    /**
     * Get all comments for a specific post.
     *
     * @param postId the ID of the post
     * @return list of CommentDto
     */
    public List<Comment> getCommentsByPostId(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return commentRepository.findAllByPostId(postId);
    }

    /**
     * Create a new comment for a specific post.
     * @param postId   the ID of the post
     * @return the created CommentDto
     */
    public Comment createComment(String postId, Comment comment, User author) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        comment.setPost(post);
        comment.setAuthor(author);
        comment.setLikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        PostStats stats = post.getStats();
        stats.setComments(stats.getComments() + 1);
        postStatsRepository.save(stats);
        post.setStats(stats);
        postRepository.save(post);

        return commentRepository.save(comment);
    }
}