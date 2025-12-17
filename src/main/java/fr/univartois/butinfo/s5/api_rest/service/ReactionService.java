package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.ReactionRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReactionService(ReactionRepository reactionRepository, PostRepository postRepository, UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<Reaction> getReactionsByPostId(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }
        return reactionRepository.findAllByPostId(postId);
    }

    public Reaction createReaction(String postId, Reaction reaction, String userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        if (existingReaction.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà réagi à ce post");
        }

        reaction.setPost(post);
        reaction.setUser(user);
        reaction.setCreatedAt(LocalDateTime.now());

        return reactionRepository.save(reaction);
    }

    public void deleteReaction(String postId, String userId) {
        if(!postRepository.existsById(postId)) {
              throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }
        reactionRepository.deleteByPostIdAndUserId(postId, userId);

    }
}