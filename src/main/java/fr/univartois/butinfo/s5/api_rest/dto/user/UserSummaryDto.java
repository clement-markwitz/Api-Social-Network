package fr.univartois.butinfo.s5.api_rest.dto.user;

/**
 * DTO (output) providing a summary of user information for contexts
 * (ex : messages, comments) where full user details are not required.
 */
public record UserSummaryDto(
        String id,
        String pseudo,
        String avatarUrl
) {
}