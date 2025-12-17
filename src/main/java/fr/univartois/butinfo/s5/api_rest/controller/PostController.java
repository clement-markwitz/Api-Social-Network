package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostUpdateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.reaction.ReactionDto;
import fr.univartois.butinfo.s5.api_rest.mapper.CommentMapper;
import fr.univartois.butinfo.s5.api_rest.model.Comment;
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
    private final CommentMapper commentMapper;

    public PostController(PostService postService , CommentService commentService, ReactionService reactionService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
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

    @GetMapping("/{idPost}")
    public ResponseEntity<PostDto> getPostById(@PathVariable String idPost) {
        return ResponseEntity.ok(postService.getPostById(idPost));
    }

    @PutMapping("/{idPost}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable String idPost,
            @Valid @RequestBody PostUpdateDto updateDto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(postService.updatePost(idPost, updateDto, user.getId()));
    }

    @DeleteMapping("/{idPost}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable String idPost,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        postService.deletePost(idPost, user.getId());
    }

    // Methode pour les reaction d'un post

    @GetMapping("/{idPost}/reactions")
    public List<ReactionDto> getReactions(@PathVariable String idPost) {
        return reactionService.getReactionsByPostId(idPost);
    }

    @PostMapping("/{idPost}/reactions")
    public ResponseEntity<ReactionDto> addReaction(
            @PathVariable String idPost,
            @Valid @RequestBody ReactionCreateDto dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reactionService.createReaction(idPost, dto, user.getId()));
    }

    @DeleteMapping("/{idPost}/reactions/{reactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(@PathVariable String id, @PathVariable String reactionId) {
        reactionService.deleteReaction(reactionId);
    }

    // Methode pour les commentaires d'un post

    @GetMapping("/{idPost}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable String idPost) {

        List<Comment> comments = commentService.getCommentsByPostId(idPost);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        return ResponseEntity.ok(commentDtos);
    }

    @PostMapping("/{idPost}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable String idPost,
            @Valid @RequestBody CommentCreateDto dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Comment commentEntity = commentMapper.toEntity(dto);
        Comment savedComment = commentService.createComment(idPost, commentEntity, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentMapper.toDto(savedComment));
    }
}
