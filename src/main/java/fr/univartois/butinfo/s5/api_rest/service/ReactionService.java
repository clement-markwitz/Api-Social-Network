package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionDto;
import fr.univartois.butinfo.s5.api_rest.mapper.ReactionMapper;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.ReactionRepository;
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
    private final ReactionMapper reactionMapper;

    public ReactionService(ReactionRepository reactionRepository, PostRepository postRepository, ReactionMapper reactionMapper) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.reactionMapper = reactionMapper;
    }

    public List<ReactionDto> getReactionsByPostId(String postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }
        return reactionRepository.findAllByPostId(postId).stream()
                .map(reactionMapper::toDto)
                .toList();
    }

    public ReactionDto createReaction(String postId, ReactionCreateDto dto, String userId) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable");
        }

        // Verifier si l'utilisateur a deja reagi a ce post
        Optional<Reaction> existingReaction = reactionRepository.findByPostIdAndUserId(postId, userId);
        if (existingReaction.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà réagi à ce post");
        }

        Reaction reaction = reactionMapper.toEntity(dto);

        //  On associe le post et l'utilisateur
        Post post = new Post();
        post.setId(postId);
        reaction.setPost(post);

        User user = new User();
        user.setId(userId);
        reaction.setUser(user);

        reaction.setCreatedAt(LocalDateTime.now());

        Reaction saved = reactionRepository.save(reaction);
        return reactionMapper.toDto(saved);
    }

    public void deleteReaction(String reactionId) {
        if (!reactionRepository.existsById(reactionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Réaction introuvable");
        }
        reactionRepository.deleteById(reactionId);
    }
}