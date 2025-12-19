package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.conversation.ConversationDto;
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


    /**
     * Convert a ConversationCreateDto to a Conversation entity.
     * @param dto the ConversationCreateDto
     * @return the Conversation entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true) // Géré par le service
    @Mapping(target = "members", ignore = true)   // Géré par le service
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Conversation toEntity(ConversationCreateDto dto);

    /**
     * Convert a Conversation entity to a ConversationSummaryDto.
     * @param conversation the Conversation entity
     * @param lastMessage the last Message in the conversation
     * @param currentUserId the ID of the current user (for context)
     * @return the ConversationSummaryDto
     */
    @Mapping(source = "conversation.id", target = "id")
    @Mapping(source = "conversation.name", target = "name")
    @Mapping(source = "lastMessage", target = "lastMessage")
    ConversationSummaryDto toSummaryDto(Conversation conversation, Message lastMessage, @Context String currentUserId);

    /**
     * Convert a Conversation entity to a ConversationDto.
     * @param conversation the Conversation entity
     * @return the ConversationDto
     */
    ConversationDto toDto(Conversation conversation);
}