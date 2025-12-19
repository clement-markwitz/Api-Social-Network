package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.*;
import fr.univartois.butinfo.s5.api_rest.repository.CommentRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.ReactionRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing reactions to posts.
 */
@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    /**
     * Constructor for ReactionService.
     *
     * @param reactionRepository the reaction repository
     * @param postRepository   the post repository
     * @param userRepository  the user repository
     */
    public ReactionService(ReactionRepository reactionRepository, PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Retrieves all reactions for a given post ID.
     *
     * @param postId the ID of the post
     * @return a list of reactions associated with the post
     */
    public List<Reaction> getReactionsByPostId(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }
        return reactionRepository.findAllByPostId(postId);
    }

    /**
     * Creates a new reaction for a post by a user.
     *
     * @param postId the ID of the post
     * @param reaction the reaction to be created
     * @param userId the ID of the user creating the reaction
     * @return the created reaction
     */
    public Reaction createReaction(String postId, Reaction reaction, String userId) {

        Post post = postRepository.findById(postId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable"));

        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        if (existingReaction.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà réagi à ce post");
        }

        reaction.setPost(post);
        reaction.setUser(user);
        reaction.setCreatedAt(LocalDateTime.now());

        return reactionRepository.save(reaction);
    }

    /**
     * Deletes a reaction for a post by a user.
     *
     * @param postId the ID of the post
     * @param userId the ID of the user whose reaction is to be deleted
     */
    public void deleteReaction(String postId, String userId) {
        if(!postRepository.existsById(postId)) {
              throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }
        reactionRepository.deleteByPostIdAndUserId(postId, userId);

    }

    /**
     * Likes a comment of a post by a user.
     *
     * @param commentId the ID of the comment
     * @param userId the ID of the user liking the comment
     */
    public void likeACommentOfAPost(String commentId, String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commentaire introuvable"));


        Optional<Reaction> existingReaction = reactionRepository.findByCommentIdAndUserId(commentId, userId);

        if (existingReaction.isPresent()) {
            reactionRepository.delete(existingReaction.get());
            return;
        }


        Reaction reaction = new Reaction();
        reaction.setComment(comment);
        reaction.setUser(user);
        reaction.setType(ReactionType.LIKE);
        reaction.setCreatedAt(LocalDateTime.now());

        reactionRepository.save(reaction);
    }
}