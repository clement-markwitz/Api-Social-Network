package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations/{conversationId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    private String getCurrentUserId() {
        return "694026c224866417681f8a36";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto send(@PathVariable String conversationId, @RequestBody @Valid MessageCreateDto dto) {
        return messageService.sendMessage(conversationId, dto, getCurrentUserId());
    }

    @GetMapping
    public List<MessageDto> getAll(@PathVariable String conversationId) {
        return messageService.getMessages(conversationId, getCurrentUserId());
    }
}