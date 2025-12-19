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
import fr.univartois.butinfo.s5.api_rest.model.Comment;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.Reaction;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.CommentService;
import fr.univartois.butinfo.s5.api_rest.service.PostService;
import fr.univartois.butinfo.s5.api_rest.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing posts, including creation, retrieval, updating, deletion,
 * as well as handling reactions and comments associated with posts.
 */
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

    /**
     * Create a new post.
     *
     * @param createDto      Data transfer object containing post creation details
     * @param authentication Authentication object containing the current user
     * @return ResponseEntity with the created PostDto
     */
    @Operation(summary = "Create a new post", description = "Allows an authenticated user to create a new post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid post creation data")
    })
    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Post post = postMapper.toEntity(createDto);
        Post savedPost = postService.createPost(post, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDto(savedPost));
    }

    /**
     * List all posts.
     *
     * @return List of PostDto
     */
    @GetMapping
    @Operation(summary = "List all posts", description = "Retrieves a list of all posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts list retrieved successfully")
    })
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts().stream()
                .map(postMapper::toDto).toList();
    }

    /**
     * Retrieve a post by ID.
     *
     * @param idPost ID of the post to retrieve
     * @return ResponseEntity with the PostDto
     */
    @GetMapping("/{idPost}")
    @Operation(summary = "Get a post by ID", description = "Retrieves a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<PostDto> getPostById(@PathVariable String idPost) {
        Post post = postService.getPostById(idPost);
        return ResponseEntity.ok(postMapper.toDto(post));
    }

    /**
     * Search for posts based on a query string.
     *
     * @param query The search query string
     * @return ResponseEntity with the list of PostDto matching the search criteria
     */
    @GetMapping("/search")
    @Operation(summary = "Search posts", description = "Searches for posts based on a query string. If the query is empty, returns all posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    })
    public ResponseEntity<List<PostDto>> searchPosts(@RequestParam("query") String query) {
        List<Post> posts;
        if (query == null || query.isBlank()) {
            posts = postService.getAllPosts();
        } else {
            posts = postService.searchPosts(query);
        }
        return ResponseEntity.ok(posts.stream().map(postMapper::toDto).toList());
    }

    /**
     * Update an existing post.
     *
     * @param idPost         ID of the post to update
     * @param updateDto      Data transfer object containing post update details
     * @param authentication Authentication object containing the current user
     * @return ResponseEntity with the updated PostDto
     */
    @PutMapping("/{idPost}")
    @Operation(summary = "Update a post", description = "Allows the author of a post to update its content.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not the author of the post"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "400", description = "Invalid post update data")
    })
    public ResponseEntity<PostDto> updatePost(
            @PathVariable String idPost,
            @Valid @RequestBody PostUpdateDto updateDto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Post existingPost = postService.getPostById(idPost);
        if (!existingPost.getAuthor().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the author of this post.");
        }

        postMapper.updatePostFromDto(updateDto, existingPost);
        Post updatedPost = postService.updatePost(existingPost);

        return ResponseEntity.ok(postMapper.toDto(updatedPost));
    }

    /**
     * Delete a post.
     *
     * @param idPost         ID of the post to delete
     * @param authentication Authentication object containing the current user
     * @return ResponseEntity with no content
     */
    @Operation(summary = "Delete a post", description = "Allows the author of a post or an admin to delete the post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not the author of the post or an admin"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    @DeleteMapping("/{idPost}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String idPost,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Post existingPost = postService.getPostById(idPost);

        if (!existingPost.getAuthor().getId().equals(user.getId()) && !user.getRole().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the author of this post or an admin.");
        }

        postService.deletePost(existingPost);
        return ResponseEntity.noContent().build();
    }

    // Methode pour les reaction d'un post

    /**
     * Get reactions of a post.
     *
     * @param idPost ID of the post
     * @return List of ReactionDto
     */
    @GetMapping("/{idPost}/reactions")
    @Operation(summary = "List reactions of a post", description = "Retrieves the list of reactions associated with a post specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of reactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public List<ReactionDto> getReactions(@PathVariable String idPost) {
        return reactionService.getReactionsByPostId(idPost).stream()
                .map(reactionMapper::toDto)
                .toList();
    }

    /**
     * Add a reaction to a post.
     *
     * @param idPost         ID of the post to react to
     * @param dto            Data transfer object containing reaction creation details
     * @param authentication Authentication object containing the current user
     * @return ResponseEntity with the created ReactionDto
     */
    @PostMapping("/{idPost}/reactions")
    @Operation(summary = "Ajouter une réaction à un post", description = "Permet à l'utilisateur authentifié d'ajouter une réaction à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reaction added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid reaction creation data"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public ResponseEntity<ReactionDto> addReaction(
            @PathVariable String idPost,
            @Valid @RequestBody ReactionCreateDto dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Reaction reactionEntity = reactionMapper.toEntity(dto);
        Reaction savedReaction = reactionService.createReaction(idPost, reactionEntity, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reactionMapper.toDto(savedReaction));
    }

    /**
     * Delete a reaction from a post.
     *
     * @param idPost         ID of the post
     * @param authentication Authentication object containing the current user
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{idPost}/reactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a reaction from a post", description = "Allows the author of a reaction to delete their reaction from a specified post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reaction deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Post or reaction not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden: User is not the author of the reaction")
    })
    public ResponseEntity<Void> deleteReaction(@PathVariable String idPost,
                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        reactionService.deleteReaction(idPost, user.getId());
        return ResponseEntity.noContent().build();
    }

    // Methode pour les commentaires d'un post

    /**
     * Get comments of a post.
     *
     * @param idPost ID of the post
     * @return ResponseEntity with the list of CommentDto
     */
    @GetMapping("/{idPost}/comments")
    @Operation(summary = "List comments of a post", description = "Récupère la liste des commentaires associés à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of comments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    public  ResponseEntity<List<CommentDto>> getComments(@PathVariable String idPost) {
        List<Comment> comments = commentService.getCommentsByPostId(idPost);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        return ResponseEntity.ok(commentDtos);
    }

    /**
     * Add a comment to a post.
     *
     * @param idPost         ID of the post to comment on
     * @param dto            Data transfer object containing comment creation details
     * @param authentication Authentication object containing the current user
     * @return ResponseEntity with the created CommentDto
     */
    @PostMapping("/{idPost}/comments")
    @Operation(summary = "Add a comment to a post", description = "Allows an authenticated user to add a comment to a post specified by its ID.")
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

    /**
     * Like or Unlike a comment.
     *
     * @param idComment ID of the comment to like or unlike
     * @param authentication Authentication object containing the current user
     * @return ResponseEntity with appropriate status code
     */
    @PostMapping("/comments/like/{idComment}")
    @Operation(summary = "Like or Unlike a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment liked/unliked successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> likeComment(@PathVariable String idComment, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        try {
            reactionService.likeACommentOfAPost(idComment, currentUser.getId());
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
