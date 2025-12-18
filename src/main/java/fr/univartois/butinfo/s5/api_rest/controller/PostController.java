package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostUpdateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionDto;
import fr.univartois.butinfo.s5.api_rest.mapper.CommentMapper;
import fr.univartois.butinfo.s5.api_rest.mapper.PostMapper;
import fr.univartois.butinfo.s5.api_rest.mapper.ReactionMapper;
import fr.univartois.butinfo.s5.api_rest.model.*;
import fr.univartois.butinfo.s5.api_rest.service.CommentService;
import fr.univartois.butinfo.s5.api_rest.service.PostService;
import fr.univartois.butinfo.s5.api_rest.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final ReactionMapper reactionMapper;

    public PostController(PostService postService, ReactionService reactionService, CommentService commentService,
                          PostMapper postMapper, CommentMapper commentMapper, ReactionMapper reactionMapper) {
        this.postService = postService;
        this.reactionService = reactionService;
        this.commentService = commentService;
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.reactionMapper = reactionMapper;
    }

    // --- CRUD POSTS ---

    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostCreateDto createDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Post post = postMapper.toEntity(createDto);
        Post savedPost = postService.createPost(post, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDto(savedPost));
    }

    @GetMapping
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts().stream().map(postMapper::toDto).toList();
    }

    @GetMapping("/{idPost}")
    public ResponseEntity<PostDto> getPostById(@PathVariable String idPost) {
        Post post = postService.getPostById(idPost);
        return ResponseEntity.ok(postMapper.toDto(post));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam("query") String query) {
        List<Post> posts;
        if (query == null || query.isBlank()) {
            posts = postService.getAllPosts();
        } else {
            posts = postService.searchPosts(query);
        }
        return ResponseEntity.ok(posts.stream().map(postMapper::toDto).toList());
    }

    @PutMapping("/{idPost}")
    public ResponseEntity<PostDto> updatePost(@PathVariable String idPost, @Valid @RequestBody PostUpdateDto updateDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Post existingPost = postService.getPostById(idPost);
        if (!existingPost.getAuthor().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        postMapper.updatePostFromDto(updateDto, existingPost);
        Post updatedPost = postService.updatePost(existingPost);
        return ResponseEntity.ok(postMapper.toDto(updatedPost));
    }

    @DeleteMapping("/{idPost}")
    public ResponseEntity<Void> deletePost(@PathVariable String idPost, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Post existingPost = postService.getPostById(idPost);
        if (!existingPost.getAuthor().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        postService.deletePost(existingPost);
        return ResponseEntity.noContent().build();
    }

    // --- REACTIONS (POSTS) ---

    @GetMapping("/{idPost}/reactions")
    public List<ReactionDto> getReactions(@PathVariable String idPost) {
        return reactionService.getReactionsByPostId(idPost).stream()
                .map(reactionMapper::toDto)
                .toList();
    }

    @PostMapping("/{idPost}/reactions")
    public ResponseEntity<ReactionDto> addReaction(@PathVariable String idPost, @Valid @RequestBody ReactionCreateDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Reaction reactionEntity = reactionMapper.toEntity(dto);
        Reaction savedReaction = reactionService.createReaction(idPost, reactionEntity, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reactionMapper.toDto(savedReaction));
    }

    @DeleteMapping("/{idPost}/reactions")
    public ResponseEntity<Void> deleteReaction(@PathVariable String idPost, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        reactionService.deleteReaction(idPost, user.getId());
        return ResponseEntity.noContent().build();
    }

    // --- NOUVEAU : TOGGLE LIKE POST ---
    @PostMapping("/{idPost}/like")
    @Operation(summary = "Liker/Déliker un post", description = "Ajoute un like ou le retire si déjà présent.")
    public ResponseEntity<Void> togglePostLike(@PathVariable String idPost, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        reactionService.toggleReaction(idPost, user.getId(), ReactionType.LIKE);
        return ResponseEntity.ok().build();
    }

    // --- COMMENTAIRES ---

    @GetMapping("/{idPost}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable String idPost) {
        List<Comment> comments = commentService.getCommentsByPostId(idPost);
        return ResponseEntity.ok(comments.stream().map(commentMapper::toDto).toList());
    }

    @PostMapping("/{idPost}/comments")
    public ResponseEntity<CommentDto> addComment(@PathVariable String idPost, @Valid @RequestBody CommentCreateDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Comment commentEntity = commentMapper.toEntity(dto);
        Comment savedComment = commentService.createComment(idPost, commentEntity, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toDto(savedComment));
    }

    // --- NOUVEAU : TOGGLE LIKE COMMENTAIRE ---
    @PostMapping("/{idPost}/comments/{idComment}/like")
    @Operation(summary = "Liker/Déliker un commentaire", description = "Ajoute ou retire l'utilisateur de la liste des likes du commentaire.")
    public ResponseEntity<Void> toggleCommentLike(@PathVariable String idPost, @PathVariable String idComment, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        // On pourrait vérifier que idComment appartient bien à idPost ici si besoin
        commentService.toggleLikeComment(idComment, user.getId());
        return ResponseEntity.ok().build();
    }
}