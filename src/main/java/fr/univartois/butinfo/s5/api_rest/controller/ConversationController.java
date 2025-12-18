package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.ConversationMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.ConversationService;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing conversations and their memberships.
 * <p>
 * This controller handles logic for creating, joining, leaving, and managing members of conversations.
 * Security checks (initiator rights, membership status) are performed here.
 * </p>
 */
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserService userService; // Remplacement de UserRepository par UserService
    private final ConversationMapper conversationMapper;

    /**
     * Utility method to retrieve the ID of the currently authenticated user.
     *
     * @param authentication The Spring Security authentication object containing the user's principal.
     * @return The unique ID of the authenticated user.
     * @throws ResponseStatusException if the user is not authenticated (401).
     */
    private String getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        // On utilise le userService qui gère déjà l'exception si l'user n'est pas trouvé
        // loadUserByUsername retourne un UserDetails, on le cast en User (notre modèle)
        User user = (User) userService.loadUserByUsername(authentication.getName());
        return user.getId();
    }

    /**
     * Creates a new conversation.
     *
     * @param dto            The data transfer object containing the conversation name and initial member IDs.
     * @param authentication The authentication object to identify the initiator.
     * @return The created {@link Conversation} entity.
     * @throws ResponseStatusException if the initiator cannot be found in the database.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une conversertion", description = "Créer une nouvelle conversation avec des membres initiaux.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversation créée avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur initiateur ou membre non trouvé")
    })
    public Conversation create(
            @RequestBody @Valid ConversationCreateDto dto,
            Authentication authentication) {

        Conversation conversation = conversationMapper.toEntity(dto);
        String currentUserId = getCurrentUserId(authentication);

        // Utilisation du service pour récupérer l'initiateur (lève une exception si non trouvé)
        User initiator = userService.getById(currentUserId);
        conversation.setInitiator(initiator);

        List<String> memberIds = new ArrayList<>(dto.memberIds());
        if (!memberIds.contains(currentUserId)) {
            memberIds.add(currentUserId);
        }

        // Récupération via le service (boucle stream pour garantir que chaque ID existe via getById)
        List<User> members = memberIds.stream()
                .map(userService::getById)
                .toList();

        return conversationService.createConversation(conversation, members);
    }

    /**
     * Retrieves all conversations where the authenticated user is a member.
     *
     * @param authentication The authentication object to identify the current user.
     * @return A list of {@link ConversationSummaryDto} representing the user's conversations.
     */
    @GetMapping
    @Operation(summary = "Lister mes conversations", description = "Récupère toutes les conversations dont l'utilisateur authentifié est membre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des conversations récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié")
    })
    public List<ConversationSummaryDto> getAll(Authentication authentication) {
        return conversationService.getMyConversations(getCurrentUserId(authentication));
    }

    /**
     * Retrieves the details of a specific conversation by its ID.
     *
     * @param id The unique identifier of the conversation.
     * @return The {@link Conversation} entity.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une conversation", description = "Récupère les détails d'une conversation spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Conversation non trouvée")
    })
    public Conversation getOne(@PathVariable String id) {
        return conversationService.findById(id);
    }

    /**
     * Allows the authenticated user to join an existing conversation.
     *
     * @param id             The unique identifier of the conversation to join.
     * @param authentication The authentication object to identify the user joining.
     * @throws ResponseStatusException if the user is already a member (409) or user not found (404).
     */
    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Rejoindre une conversation", description = "Permet à l'utilisateur authentifié de rejoindre une conversation existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rejoint la conversation avec succès"),
            @ApiResponse(responseCode = "409", description = "L'utilisateur est déjà membre de la conversation"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public void join(
            @PathVariable String id,
            Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);
        Conversation conversation = conversationService.findById(id);

        boolean alreadyMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(currentUserId));
        if (alreadyMember) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already a member of this conversation");
        }

        // Utilisation du UserService
        User user = userService.getById(currentUserId);

        conversationService.joinConversation(conversation, user);
    }

    /**
     * Allows the authenticated user to leave a conversation.
     *
     * @param id             The unique identifier of the conversation to leave.
     * @param authentication The authentication object to identify the user leaving.
     * @throws ResponseStatusException if the user is not a member of the conversation (403).
     */
    @PostMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitter une conversation", description = "Permet à l'utilisateur authentifié de quitter une conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Quitte la conversation avec succès"),
            @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas membre de la conversation")
    })
    public void leave(
            @PathVariable String id,
            Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);
        Conversation conversation = conversationService.findById(id);

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(currentUserId));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this conversation");
        }

        conversationService.leaveConversation(conversation, currentUserId);
    }

    /**
     * Adds a list of users to an existing conversation.
     * <p>
     * <b>Restriction:</b> Only the initiator of the conversation can perform this action.
     * </p>
     *
     * @param id             The unique identifier of the conversation.
     * @param userIdsToAdd   A list of user IDs to add to the conversation.
     * @param authentication The authentication object to verify if the user is the initiator.
     * @throws ResponseStatusException if the user is not the initiator (403).
     */
    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Ajouter des membres à une conversation", description = "Permet à l'initiateur de la conversation d'ajouter des membres.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membres ajoutés avec succès"),
            @ApiResponse(responseCode = "403", description = "Seul l'initiateur peut ajouter des membres"),
            @ApiResponse(responseCode = "404", description = "Un des utilisateurs à ajouter n'existe pas")
    })
    public void addMembers(
            @PathVariable String id,
            @RequestBody List<String> userIdsToAdd,
            Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);
        Conversation conversation = conversationService.findById(id);

        if (!conversation.getInitiator().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the initiator can add members");
        }

        // Utilisation du UserService pour récupérer les entités User
        List<User> usersToAdd = userIdsToAdd.stream()
                .map(userService::getById)
                .toList();

        conversationService.addMembers(conversation, usersToAdd);
    }

    /**
     * Removes a specific member from a conversation (kick).
     * <p>
     * <b>Restriction:</b> Only the initiator of the conversation can perform this action.
     * </p>
     *
     * @param id             The unique identifier of the conversation.
     * @param memberId       The unique identifier of the member to remove.
     * @param authentication The authentication object to verify if the user is the initiator.
     * @throws ResponseStatusException if the user is not the initiator (403), tries to kick themselves (400), or target not found (404).
     */
    @DeleteMapping("/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Expulser un membre d'une conversation", description = "Permet à l'initiateur de la conversation d'expulser un membre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Membre expulsé avec succès"),
            @ApiResponse(responseCode = "403", description = "Seul l'initiateur peut expulser des membres"),
            @ApiResponse(responseCode = "400", description = "Vous ne pouvez pas vous expulser vous-même. Utilisez 'quitter' à la place."),
            @ApiResponse(responseCode = "404", description = "Ce membre n'est pas dans la conversation")
    })
    public void kickMember(
            @PathVariable String id,
            @PathVariable String memberId,
            Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);
        Conversation conversation = conversationService.findById(id);

        if (!conversation.getInitiator().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the initiator can kick members");
        }

        if (memberId.equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot kick yourself. Use 'leave' instead.");
        }

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(memberId));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This user is not in the conversation");
        }

        conversationService.kickMember(conversation, memberId);
    }
}