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

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conversation", ignore = true) // Géré par le service
    @Mapping(target = "sender", ignore = true)       // Géré par le service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "readBy", ignore = true)
    Message toEntity(MessageCreateDto dto);

    @Mapping(source = "conversation.id", target = "conversationId")
    MessageDto toDto(Message message);

    List<MessageDto> toDtoList(List<Message> messages);

    @Mapping(target = "text", source = "message.text")
    @Mapping(target = "createdAt", source = "message.createdAt")
    @Mapping(target = "isRead", expression = "java(isReadByCurrentUser(message, currentUserId))")
    MessageSummaryDto toSummaryDto(Message message, @Context String currentUserId);

    @Mapping(source = "user", target = "user")
    ReadReceiptDto toReadReceiptDto(ReadReceipt readReceipt);

    default boolean isReadByCurrentUser(Message message, String currentUserId) {
        if (message == null || message.getReadBy() == null) return false;
        return message.getReadBy().stream()
                .anyMatch(r -> r.getUser() != null && r.getUser().getId().equals(currentUserId));
    }
}