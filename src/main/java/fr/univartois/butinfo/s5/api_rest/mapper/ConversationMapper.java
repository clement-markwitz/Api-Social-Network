package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for Conversation entities and DTOs.
 */
@Mapper(componentModel = "spring", uses = {MessageMapper.class})
public interface ConversationMapper {

    // --- Vers Entity ---

    /**
     * Convert a ConversationCreateDto to a Conversation entity.
     * @param dto
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true) // Géré par le service
    @Mapping(target = "members", ignore = true)   // Géré par le service
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Conversation toEntity(ConversationCreateDto dto);

    // --- Vers DTO ---
    /**
     * Convert a Conversation entity to a ConversationSummaryDto.
     * @param conversation
     * @param lastMessage
     * @param currentUserId
     * @return
     */
    @Mapping(source = "conversation.id", target = "id")
    @Mapping(source = "conversation.name", target = "name")
    // Appelle MessageMapper.toSummaryDto pour le dernier message
    @Mapping(source = "lastMessage", target = "lastMessage")
    ConversationSummaryDto toSummaryDto(Conversation conversation, Message lastMessage, @Context String currentUserId);
}