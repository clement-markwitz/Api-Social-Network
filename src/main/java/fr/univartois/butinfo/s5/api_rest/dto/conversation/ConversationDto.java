package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.ConversationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationDto (
    String id,
    ConversationStatus status,
    String name,
    List<UserSummaryDto> members,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){
}
