package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (output) representing a user's public profile.
 * Note : This DTO is intended for public viewing, so sensitive information is excluded.
 */
public record UserPublicProfileDto(
        String id,
        String username,
        String pseudo,
        String bio,
        String avatarUrl,
        String location,
        List<String> languages,
        InterestsDto interests,
        LocalDateTime createdAt
) {
}