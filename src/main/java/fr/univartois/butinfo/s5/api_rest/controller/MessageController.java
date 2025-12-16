package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    // UserRepository supprimé (inutile car on a déjà l'User dans l'Authentication)

    /**
     * Récupère l'ID de l'utilisateur connecté depuis le contexte de sécurité.
     * Évite un appel inutile à la base de données.
     */
    private String getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto send(
            @PathVariable String conversationId,
            @RequestBody @Valid MessageCreateDto dto,
            Authentication authentication) {
        return messageService.sendMessage(conversationId, dto, getCurrentUserId(authentication));
    }

    @GetMapping
    public List<MessageDto> getAll(
            @PathVariable String conversationId,
            Authentication authentication) {
        return messageService.getMessages(conversationId, getCurrentUserId(authentication));
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            Authentication authentication) {
        messageService.deleteMessage(conversationId, messageId, getCurrentUserId(authentication));
    }
}