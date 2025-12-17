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

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;
    private final ConversationMapper conversationMapper;

    /**
     * Méthode utilitaire pour récupérer l'ID de l'utilisateur connecté.
     */
    private String getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }
        // On récupère le username depuis le token et on cherche l'ID en base
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Conversation create(
            @RequestBody @Valid ConversationCreateDto dto,
            Authentication authentication) {

        // conversion dto en entite avec mapper
        Conversation conversation = conversationMapper.toEntity(dto);

        // 1. Récupérer l'initiateur
        User initiator = userRepository.findById(getCurrentUserId(authentication))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur connecté introuvable"));
        conversation.setInitiator(initiator);

        // 2. Récupérer les membres (Initiateur + liste des invités)
        List<String> memberIds = new ArrayList<>(dto.memberIds());
        if (!memberIds.contains(getCurrentUserId(authentication))) {
            memberIds.add(getCurrentUserId(authentication));
        }

        List<User> members = userRepository.findAllById(memberIds);
        return conversationService.createConversation(conversation, members);
    }

    @GetMapping
    public List<ConversationSummaryDto> getAll(Authentication authentication) {
        return conversationService.getMyConversations(getCurrentUserId(authentication));
    }

    @GetMapping("/{id}")
    public Conversation getOne(@PathVariable String id) {
        // Ici, on pourrait ajouter une vérification pour savoir si l'user a le droit de voir la conv
        return conversationService.getConversationById(id);
    }

    @PostMapping("/{id}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void join(
            @PathVariable String id,
            Authentication authentication) {
        conversationService.joinConversation(id, getCurrentUserId(authentication));
    }

    @PostMapping("/{id}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(
            @PathVariable String id,
            Authentication authentication) {
        conversationService.leaveConversation(id, getCurrentUserId(authentication));
    }

    /**
     * Ajouter des membres (Seul l'initiateur peut le faire).
     * Body : ["id_user_1", "id_user_2"]
     */
    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.OK)
    public void addMembers(
            @PathVariable String id,
            @RequestBody List<String> userIdsToAdd,
            Authentication authentication) {
        conversationService.addMembersToConversation(id, userIdsToAdd, getCurrentUserId(authentication));
    }

    /**
     * Exclure un membre (Seul l'initiateur peut le faire).
     */
    @DeleteMapping("/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void kickMember(
            @PathVariable String id,
            @PathVariable String memberId,
            Authentication authentication) {
        conversationService.kickMemberFromConversation(id, memberId, getCurrentUserId(authentication));
    }
}