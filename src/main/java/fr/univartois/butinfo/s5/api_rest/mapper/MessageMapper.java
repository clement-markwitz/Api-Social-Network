package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.message.MessageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.MessageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.message.ReadReceiptDto;
import fr.univartois.butinfo.s5.api_rest.model.Message;
import fr.univartois.butinfo.s5.api_rest.model.ReadReceipt;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for converting between Message entities and their DTOs.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {


    /**
     * Convert MessageCreateDto to Message entity.
     * @param dto the MessageCreateDto
     * @return the Message entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "readBy", ignore = true)
    Message toEntity(MessageCreateDto dto);

    /**
     * Convert Message entity to MessageDto.
     * @param message the Message entity
     * @return the MessageDto
     */
    @Mapping(source = "conversation.id", target = "conversationId")
    MessageDto toDto(Message message);

    /**
     * Convert list of Message entities to list of MessageDto.
     * @param messages the list of Message entities
     * @return the list of MessageDto
     */
    List<MessageDto> toDtoList(List<Message> messages);

    /**
     * Convert Message entity to MessageSummaryDto.
     * @param message the Message entity
     * @param currentUserId ID of the current user to determine read status
     * @return the MessageSummaryDto
     */
    @Mapping(target = "text", source = "message.text")
    @Mapping(target = "createdAt", source = "message.createdAt")
    @Mapping(target = "isRead", expression = "java(isReadByCurrentUser(message, currentUserId))")
    MessageSummaryDto toSummaryDto(Message message, @Context String currentUserId);

    /**
     * Convert ReadReceipt entity to ReadReceiptDto.
     * @param readReceipt the ReadReceipt entity
     * @return the ReadReceiptDto
     */
    @Mapping(source = "user", target = "user")
    ReadReceiptDto toReadReceiptDto(ReadReceipt readReceipt);

    /**
     * Determine if the message has been read by the current user.
     * @param message the Message entity
     * @param currentUserId the ID of the current user
     * @return the read status
     */
    default boolean isReadByCurrentUser(Message message, String currentUserId) {
        if (message == null || message.getReadBy() == null) return false;
        return message.getReadBy().stream()
                .anyMatch(r -> r.getUser() != null && r.getUser().getId().equals(currentUserId));
    }
}