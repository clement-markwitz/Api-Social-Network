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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller for managing posts.
 */
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

    /**
     * Create a new post.
     * @param createDto the post creation data
     * @param authentication the authentication object
     * @return ResponseEntity with created PostDto
     */
    @PostMapping
    @Operation(summary = "Create a post", description = "Allows the authenticated user to create a new post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid post creation data")
    })
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        PostDto createdPost = postService.createPost(createDto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    /**
     * Get all posts.
     * @return List of PostDto
     */
    @GetMapping
    @Operation(summary = "List all posts", description = "Retrieves a list of all posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts list retrieved successfully")
    })

    /**
     * Get all posts.
     * @return List of PostDto
     */
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    /**
     * Get a post by its ID.
     * @param id the ID of the post
     * @return ResponseEntity with PostDto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a post by ID", description = "Retrieves details of a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<?> getPostById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(postService.getPostById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Search posts by a query string.
     * @param query the search query
     * @return ResponseEntity with list of PostDto
     */
    @GetMapping("/search")
    @Operation(summary = "Search posts", description = "Searches posts containing the specified term in their title or content.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    })
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam("query") String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.ok(postService.getAllPosts());
        }
        return ResponseEntity.ok(postService.searchPosts(query));
    }

    /**
     * Update a post.
     * @param id the ID of the post to update
     * @param updateDto the post update data
     * @param authentication the authentication object
     * @return ResponseEntity with updated PostDto
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a post", description = "Allows the authenticated user to update a post they created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid post update data"),
            @ApiResponse(responseCode = "403", description = "Access denied (you are not the author of the post)"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<?> updatePost(
            @PathVariable String id,
            @Valid @RequestBody PostUpdateDto updateDto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(postService.updatePost(id, updateDto, user.getId()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Delete a post.
     * @param id the ID of the post to delete
     * @param authentication the authentication object
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a post", description = "Allows the authenticated user to delete a post they created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (you are not the author of the post)"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<?> deletePost(
            @PathVariable String id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        try {
            postService.deletePost(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // Methode pour les reaction d'un post

    /**
     * Get reactions for a post.
     * @param id the ID of the post
     * @return List of ReactionDto
     */
    @GetMapping("/{id}/reactions")
    @Operation(summary = "List reactions for a post", description = "Retrieves the list of reactions associated with a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reactions list retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public List<ReactionDto> getReactions(@PathVariable String id) {
        return reactionService.getReactionsByPostId(id);
    }

    /**
     * Add a reaction to a post.
     * @param id the ID of the post
     * @param dto the reaction creation data
     * @param authentication the authentication object
     * @return ResponseEntity with created ReactionDto
     */
    @PostMapping("/{id}/reactions")
    @Operation(summary = "Add a reaction to a post", description = "Allows the authenticated user to add a reaction to a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reaction added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reaction creation data"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<ReactionDto> addReaction(
            @PathVariable String id,
            @Valid @RequestBody ReactionCreateDto dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reactionService.createReaction(id, dto, user.getId()));
    }

    /**
     * Delete a reaction from a post.
     * @param id the ID of the post
     * @param reactionId the ID of the reaction to delete
     */
    @DeleteMapping("/{id}/reactions/{reactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a reaction from a post", description = "Allows the authenticated user to delete a reaction they added to a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reaction deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (you are not the author of the reaction)"),
            @ApiResponse(responseCode = "404", description = "Reaction not found")
    })
    public void deleteReaction(@PathVariable String id, @PathVariable String reactionId) {
        reactionService.deleteReaction(reactionId);
    }

    // Methode pour les commentaires d'un post

    /**
     * Get comments for a post.
     * @param id the ID of the post
     * @return List of CommentDto
     */
    @GetMapping("/{id}/comments")
    @Operation(summary = "List comments for a post", description = "Retrieves the list of comments associated with a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments list retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public  ResponseEntity<List<CommentDto>> getComments(@PathVariable String id) {
        List<Comment> comments = commentService.getCommentsByPostId(id);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        return ResponseEntity.ok(commentDtos);
    }

    /**
     * Add a comment to a post.
     * @param id the ID of the post
     * @param dto the comment creation data
     * @param authentication the authentication object
     * @return ResponseEntity with created CommentDto
     */
    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a post", description = "Allows the authenticated user to add a comment to a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid comment creation data"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
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
