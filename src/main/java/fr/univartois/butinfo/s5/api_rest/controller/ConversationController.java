package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.ConversationMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import fr.univartois.butinfo.s5.api_rest.service.ConversationService;
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
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    /**
     * Utility method to retrieve the ID of the currently authenticated user.
     *
     * @param authentication The Spring Security authentication object containing the user's principal.
     * @return The unique ID of the authenticated user.
     * @throws ResponseStatusException if the user is not authenticated (401) or found in the database (401).
     */
    private String getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
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
    public Conversation create(
            @RequestBody @Valid ConversationCreateDto dto,
            Authentication authentication) {

        Conversation conversation = conversationMapper.toEntity(dto);

        User initiator = userRepository.findById(getCurrentUserId(authentication))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Connected user not found"));
        conversation.setInitiator(initiator);

        List<String> memberIds = new ArrayList<>(dto.memberIds());
        if (!memberIds.contains(getCurrentUserId(authentication))) {
            memberIds.add(getCurrentUserId(authentication));
        }

        List<User> members = userRepository.findAllById(memberIds);
        return conversationService.createConversation(conversation, members);
    }

    /**
     * Retrieves all conversations where the authenticated user is a member.
     *
     * @param authentication The authentication object to identify the current user.
     * @return A list of {@link ConversationSummaryDto} representing the user's conversations.
     */
    @GetMapping
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

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

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
    public void addMembers(
            @PathVariable String id,
            @RequestBody List<String> userIdsToAdd,
            Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);
        Conversation conversation = conversationService.findById(id);

        if (!conversation.getInitiator().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the initiator can add members");
        }

        List<User> usersToAdd = userRepository.findAllById(userIdsToAdd);
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