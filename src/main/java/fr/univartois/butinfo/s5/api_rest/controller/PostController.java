package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostUpdateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionDto;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.CommentService;
import fr.univartois.butinfo.s5.api_rest.service.PostService;
import fr.univartois.butinfo.s5.api_rest.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final ReactionService reactionService;
    private final CommentService commentService;

    public PostController(PostService postService , CommentService commentService, ReactionService reactionService) {
        this.commentService = commentService;
        this.reactionService = reactionService;
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        PostDto createdPost = postService.createPost(createDto, user.getId());
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
            @Valid @RequestBody PostUpdateDto updateDto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(postService.updatePost(id, updateDto, user.getId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable String id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        postService.deletePost(id, user.getId());
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
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reactionService.createReaction(id, dto, user.getId()));
    }

    @DeleteMapping("/{id}/reactions/{reactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(@PathVariable String id, @PathVariable String reactionId) {
        reactionService.deleteReaction(reactionId);
    }

    // Methode pour les commentaires d'un post
    @GetMapping("/{id}/comments")
    public List<CommentDto> getComments(@PathVariable String id) {
        return commentService.getCommentsByPostId(id);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable String id,
            @Valid @RequestBody CommentCreateDto dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(id, dto, user.getId()));
    }
}
