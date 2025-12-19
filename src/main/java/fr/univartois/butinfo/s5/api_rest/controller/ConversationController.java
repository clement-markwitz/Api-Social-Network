package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationDto;
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
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;
    private final ConversationMapper conversationMapper;

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
    @Operation(summary = "Create a conversation", description = "Creates a new conversation with the authenticated user as the initiator.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid conversation data provided"),
            @ApiResponse(responseCode = "404", description = "One or more users not found")
    })
    public ResponseEntity<ConversationDto> create(
            @RequestBody @Valid ConversationCreateDto dto,
            Authentication authentication) {

        User initiator = (User) authentication.getPrincipal();
        Conversation conversation = conversationMapper.toEntity(dto);
        conversation.setInitiator(initiator);

        List<String> memberIds = new ArrayList<>(dto.memberIds());
        if (!memberIds.contains(initiator.getId())) {
            memberIds.add(initiator.getId());
        }
        if (memberIds.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A conversation must have at least 2 members");
        }

        List<User> members = memberIds.stream()
                .map(userService::getById)
                .toList();

        Conversation createdConversation = conversationService.createConversation(conversation, members);
        ConversationDto createdConversationDto = conversationMapper.toDto(createdConversation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConversationDto);
    }

    /**
     * Retrieves all conversations where the authenticated user is a member.
     *
     * @param authentication The authentication object to identify the current user.
     * @return A list of {@link ConversationSummaryDto} representing the user's conversations.
     */
    @GetMapping
    @Operation(summary = "List my conversations", description = "Retrieves all conversations where the authenticated user is a member.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of conversations retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated")
    })
    public List<ConversationSummaryDto> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return conversationService.getMyConversations(user.getId());
    }

    /**
     * Retrieves the details of a specific conversation by its ID.
     *
     * @param id The unique identifier of the conversation.
     * @return The {@link Conversation} entity.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a conversation", description = "Retrieves the details of a specific conversation by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Conversation not found")
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
    @Operation(summary = "Join a conversation", description = "Permet à l'utilisateur authentifié de rejoindre une conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rejoin the conversation successfully"),
            @ApiResponse(responseCode = "409", description = "User is already a member of the conversation"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public void join(
            @PathVariable String id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Conversation conversation = conversationService.findById(id);
        boolean alreadyMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (alreadyMember) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already a member of this conversation");
        }

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
    @Operation(summary = "Leave a conversation", description = "Permet à l'utilisateur authentifié de quitter une conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Left the conversation successfully"),
            @ApiResponse(responseCode = "403", description = "You are not a member of this conversation")
    })
    public void leave(
            @PathVariable String id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Conversation conversation = conversationService.findById(id);

        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this conversation");
        }

        conversationService.leaveConversation(conversation, user.getId());
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
    @Operation(summary = "Add members to a conversation", description = "The initiator of the conversation can add new members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member added successfully"),
            @ApiResponse(responseCode = "403", description = "Only the initiator can add members")
    })
    public void addMembers(
            @PathVariable String id,
            @RequestBody List<String> userIdsToAdd,
            Authentication authentication) {
        User initiator = (User) authentication.getPrincipal();
        Conversation conversation = conversationService.findById(id);

        if (!conversation.getInitiator().getId().equals(initiator.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the initiator can add members");
        }

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
    @Operation(summary = "Kick a member from a conversation", description = "The initiator of the conversation can kick members.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Member kicked successfully"),
            @ApiResponse(responseCode = "403", description = "Only the initiator can kick members"),
            @ApiResponse(responseCode = "400", description = "You cannot kick yourself. Use 'leave' instead."),
            @ApiResponse(responseCode = "404", description = "This user is not in the conversation")
    })
    public void kickMember(
            @PathVariable String id,
            @PathVariable String memberId,
            Authentication authentication) {

        User initiator = (User) authentication.getPrincipal();
        Conversation conversation = conversationService.findById(id);

        if (!conversation.getInitiator().getId().equals(initiator.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the initiator can kick members");
        }

        if (memberId.equals(initiator.getId())) {
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