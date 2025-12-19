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

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            @Valid @RequestBody PostCreateDto createDto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        Post post = postMapper.toEntity(createDto);
        Post savedPost = postService.createPost(post, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDto(savedPost));
    }

    @GetMapping
    @Operation(summary = "Lister tous les posts", description = "Récupère une liste de tous les posts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des posts récupérée avec succès")
    })
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts().stream()
                .map(postMapper::toDto).toList();
    }

    @GetMapping("/{idPost}")
    @Operation(summary = "Récupérer un post par ID", description = "Récupère les détails d'un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
    })
    public ResponseEntity<PostDto> getPostById(@PathVariable String idPost) {
        Post post = postService.getPostById(idPost);
        return ResponseEntity.ok(postMapper.toDto(post));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des posts", description = "Recherche des posts contenant le terme spécifié dans leur titre ou contenu.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de la recherche récupérés avec succès")
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

    @PutMapping("/{idPost}")
    @Operation(summary = "Mettre à jour un post", description = "Permet à l'utilisateur authentifié de mettre à jour un post qu'il a créé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour de post invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (l'utilisateur n'est pas l'auteur du post)"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
    })

    public ResponseEntity<PostDto> updatePost(
            @PathVariable String idPost,
            @Valid @RequestBody PostUpdateDto updateDto,
            Authentication authentication) {

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
    public ResponseEntity<Void> deletePost(
            @PathVariable String idPost,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Post existingPost = postService.getPostById(idPost);

        if (!existingPost.getAuthor().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.deletePost(existingPost);
        return ResponseEntity.noContent().build();
    }

    // Methode pour les reaction d'un post

    @GetMapping("/{idPost}/reactions")
    @Operation(summary = "Lister les réactions d'un post", description = "Récupère la liste des réactions associées à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des réactions récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
    })
    public List<ReactionDto> getReactions(@PathVariable String idPost) {
        return reactionService.getReactionsByPostId(idPost).stream()
                .map(reactionMapper::toDto)
                .toList();
    }

    @PostMapping("/{idPost}/reactions")
    @Operation(summary = "Ajouter une réaction à un post", description = "Permet à l'utilisateur authentifié d'ajouter une réaction à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Réaction ajoutée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de création de réaction invalides"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
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

    @DeleteMapping("/{idPost}/reactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une réaction d'un post", description = "Permet à l'utilisateur authentifié de supprimer une réaction qu'il a ajoutée à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Réaction supprimée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (l'utilisateur n'est pas l'auteur de la réaction)"),
            @ApiResponse(responseCode = "404", description = "Réaction non trouvée")
    })
    public ResponseEntity<Void> deleteReaction(@PathVariable String idPost,
                                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        reactionService.deleteReaction(idPost, user.getId());
        return ResponseEntity.noContent().build();
    }

    // Methode pour les commentaires d'un post

    @GetMapping("/{idPost}/comments")
    @Operation(summary = "Lister les commentaires d'un post", description = "Récupère la liste des commentaires associés à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des commentaires récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
    })
    public  ResponseEntity<List<CommentDto>> getComments(@PathVariable String idPost) {
        List<Comment> comments = commentService.getCommentsByPostId(idPost);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .toList();

        return ResponseEntity.ok(commentDtos);
    }

    @PostMapping("/{idPost}/comments")
    @Operation(summary = "Ajouter un commentaire à un post", description = "Permet à l'utilisateur authentifié d'ajouter un commentaire à un post spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commentaire ajouté avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de création de commentaire invalides"),
            @ApiResponse(responseCode = "404", description = "Post non trouvé")
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
