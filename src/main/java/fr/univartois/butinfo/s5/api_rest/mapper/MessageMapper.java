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

    // --- Vers Entity (Création) ---

    /**
     * Convert MessageCreateDto to Message entity.
     * @param dto
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conversation", ignore = true) // Géré par le service
    @Mapping(target = "sender", ignore = true)       // Géré par le service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "readBy", ignore = true)
    Message toEntity(MessageCreateDto dto);

    // --- Vers DTO Complet (Chat) ---
    // MapStruct utilise UserMapper pour convertir message.sender (User) -> sender (UserSummaryDto)
    /**
     * Convert Message entity to MessageDto.
     * @param message
     * @return
     */
    @Mapping(source = "conversation.id", target = "conversationId")
    MessageDto toDto(Message message);

    /**
     * Convert list of Message entities to list of MessageDto.
     * @param messages
     * @return
     */
    List<MessageDto> toDtoList(List<Message> messages);

    // --- Vers DTO Résumé (Liste Conversations) ---
    /**
     * Convert Message entity to MessageSummaryDto.
     * @param message
     * @param currentUserId ID of the current user to determine read status
     * @return
     */
    @Mapping(target = "text", source = "message.text")
    @Mapping(target = "createdAt", source = "message.createdAt")
    @Mapping(target = "isRead", expression = "java(isReadByCurrentUser(message, currentUserId))")
    MessageSummaryDto toSummaryDto(Message message, @Context String currentUserId);

    // --- ReadReceipt ---
    /**
     * Convert ReadReceipt entity to ReadReceiptDto.
     * @param readReceipt
     * @return
     */
    @Mapping(source = "user", target = "user")
    ReadReceiptDto toReadReceiptDto(ReadReceipt readReceipt);

    // Helper pour calculer 'isRead'
    /**
     * Determine if the message has been read by the current user.
     * @param message
     * @param currentUserId
     * @return
     */
    default boolean isReadByCurrentUser(Message message, String currentUserId) {
        if (message == null || message.getReadBy() == null) return false;
        return message.getReadBy().stream()
                .anyMatch(r -> r.getUser() != null && r.getUser().getId().equals(currentUserId));
    }
}