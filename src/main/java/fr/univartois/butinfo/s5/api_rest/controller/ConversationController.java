package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    private String getCurrentUserId() {
        return "694026c224866417681f8a36";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Conversation create(@RequestBody @Valid ConversationCreateDto dto) {
        return conversationService.createConversation(dto, getCurrentUserId());
    }

    @GetMapping
    public List<ConversationSummaryDto> getAll() {
        return conversationService.getMyConversations(getCurrentUserId());
    }

    @GetMapping("/{id}")
    public Conversation getOne(@PathVariable String id) {
        return conversationService.getConversationById(id);
    }
}