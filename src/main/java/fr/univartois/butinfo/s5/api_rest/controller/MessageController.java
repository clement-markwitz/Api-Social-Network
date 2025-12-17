package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.mapper.MessageMapper;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import fr.univartois.butinfo.s5.api_rest.service.ConversationService;
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

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    private String getCurrentUserId(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié");
        }
        return userRepository.findByUsername(authentication.getName())
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto send(
            @PathVariable String conversationId,
            @RequestBody @Valid MessageCreateDto dto,
            Authentication authentication) {
        String senderId = getCurrentUserId(authentication);
        Conversation conversation=conversationService.findById(conversationId);
        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isMember) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non membre");
        }
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expéditeur introuvable"));
        Message message = messageMapper.toEntity(dto);
        return messageService.sendMessage(conversation, message, sender);
    }

    @GetMapping
    public List<MessageDto> getAll(
            @PathVariable String conversationId,
            Authentication authentication) {
        String senderId = getCurrentUserId(authentication);
        Conversation conversation=conversationService.findById(conversationId);
        boolean isMember = conversation.getMembers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isMember) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return messageMapper.toDtoList(messageService.getMessages(conversation));
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String conversationId,
            @PathVariable String messageId,
            Authentication authentication) {
        String currentUserId = getCurrentUserId(authentication);

        Message message = messageService.findById(messageId);
        if (!message.getConversation().getId().equals(conversationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce message n'appartient pas à cette conversation");
        }
        if (!message.getSender().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous ne pouvez supprimer que vos propres messages");
        }
        messageService.deleteMessage(conversationId, message);
    }
}