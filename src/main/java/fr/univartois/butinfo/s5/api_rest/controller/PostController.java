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

    public PostController(PostService postService , CommentService commentService, ReactionService reactionService) {
        this.commentService = commentService;
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
    @Operation(summary = "Créer un post", description = "Permet à l'utilisateur authentifié de créer un nouveau post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de création de post invalides")
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
    @Operation(summary = "Lister tous les posts", description = "Récupère une liste de tous les posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des posts récupérée avec succès")
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
    @Operation(summary = "Récupérer un post par ID", description = "Récupère les détails d'un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
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
    @Operation(summary = "Rechercher des posts", description = "Recherche des posts contenant le terme spécifié dans leur titre ou contenu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de la recherche récupérés avec succès")
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
    @Operation(summary = "Mettre à jour un post", description = "Permet à l'utilisateur authentifié de mettre à jour un post qu'il a créé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour de post invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (l'utilisateur n'est pas l'auteur du post)"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
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
    @Operation(summary = "Supprimer un post", description = "Permet à l'utilisateur authentifié de supprimer un post qu'il a créé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post supprimé avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (l'utilisateur n'est pas l'auteur du post)"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
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
    @Operation(summary = "Lister les réactions d'un post", description = "Récupère la liste des réactions associées à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réactions récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
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
    @Operation(summary = "Ajouter une réaction à un post", description = "Permet à l'utilisateur authentifié d'ajouter une réaction à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réaction ajoutée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de création de réaction invalides"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
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
    @Operation(summary = "Supprimer une réaction d'un post", description = "Permet à l'utilisateur authentifié de supprimer une réaction qu'il a ajoutée à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réaction supprimée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (l'utilisateur n'est pas l'auteur de la réaction)"),
            @ApiResponse(responseCode = "404", description = "Réaction non trouvée")
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
    @Operation(summary = "Lister les commentaires d'un post", description = "Récupère la liste des commentaires associés à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des commentaires récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
    })
    public List<CommentDto> getComments(@PathVariable String id) {
        return commentService.getCommentsByPostId(id);
    }

    /**
     * Add a comment to a post.
     * @param id the ID of the post
     * @param dto the comment creation data
     * @param authentication the authentication object
     * @return ResponseEntity with created CommentDto
     */
    @PostMapping("/{id}/comments")
    @Operation(summary = "Ajouter un commentaire à un post", description = "Permet à l'utilisateur authentifié d'ajouter un commentaire à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commentaire ajouté avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de création de commentaire invalides"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
    })
    public ResponseEntity<CommentDto> addComment(
            @PathVariable String id,
            @Valid @RequestBody CommentCreateDto dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(id, dto, user.getId()));
    }
}
