package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.comment.CommentDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostUpdateDto;
import fr.univartois.butinfo.s5.api_rest.service.CommentService;
import fr.univartois.butinfo.s5.api_rest.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    private final CommentService commentService;

    public PostController(PostService postService , CommentService commentService) {
        this.commentService = commentService;
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



























    // Methode pour les commentaires d'un post

    @GetMapping("/{id}/comments")
    public List<CommentDto> getComments(@PathVariable String id) {
        return commentService.getCommentsByPostId(id);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable String id,
            @Valid @RequestBody CommentCreateDto dto,
            @RequestParam String authorId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(id, dto, authorId));
    }
}
