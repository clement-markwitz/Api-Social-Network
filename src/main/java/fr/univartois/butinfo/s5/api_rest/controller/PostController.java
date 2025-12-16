package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.post.PostCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostUpdateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionDto;
import fr.univartois.butinfo.s5.api_rest.service.PostService;
import fr.univartois.butinfo.s5.api_rest.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final ReactionService reactionService;

    public PostController(PostService postService, ReactionService reactionService) {
        this.reactionService = reactionService;
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            @RequestParam String authorId) {

        PostDto createdPost = postService.createPost(createDto, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable String id,
            @Valid @RequestBody PostUpdateDto updateDto) {
        return ResponseEntity.ok(postService.updatePost(id, updateDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable String id) {
        postService.deletePost(id);
    }

    // Methode pour les reaction d'un post

    @GetMapping("/{id}/reactions")
    public List<ReactionDto> getReactions(@PathVariable String id) {
        return reactionService.getReactionsByPostId(id);
    }

    @PostMapping("/{id}/reactions")
    public ResponseEntity<ReactionDto> addReaction(
            @PathVariable String id,
            @Valid @RequestBody ReactionCreateDto dto,
            @RequestParam String userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reactionService.createReaction(id, dto, userId));
    }

    @DeleteMapping("/{id}/reactions/{reactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(@PathVariable String id, @PathVariable String reactionId) {
        reactionService.deleteReaction(reactionId);
    }
}
