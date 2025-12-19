package fr.univartois.butinfo.s5.api_rest.dto.conversation;

import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.ConversationStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (output) for conversation information.
 *
 * @param id The unique identifier of the conversation
 * @param status The status of the conversation
 * @param name The name of the conversation
 * @param members The list of members in the conversation
 * @param createdAt The creation date of the conversation
 * @param updatedAt The last update date of the conversation
 */
public record ConversationDto (
    String id,
    ConversationStatus status,
    String name,
    List<UserSummaryDto> members,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){
}
