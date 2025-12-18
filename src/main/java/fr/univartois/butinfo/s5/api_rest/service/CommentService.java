package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Comment;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.CommentRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<Comment> getCommentsByPostId(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }
        return commentRepository.findAllByPostId(postId);
    }

    public Comment createComment(String postId, Comment comment, User author) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }

        comment.setPost(postRepository.findById(postId).orElseThrow());
        comment.setAuthor(author);
        // Initialisation de la liste des likes
        comment.setLikedBy(new HashSet<>());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    /**
     * Ajoute ou retire un like sur un commentaire.
     */
    public void toggleLikeComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));

        if (comment.getLikedBy() == null) {
            comment.setLikedBy(new HashSet<>());
        }

        if (comment.getLikedBy().contains(userId)) {
            comment.getLikedBy().remove(userId); // Dé-like
        } else {
            comment.getLikedBy().add(userId); // Like
        }

        commentRepository.save(comment);
    }
}