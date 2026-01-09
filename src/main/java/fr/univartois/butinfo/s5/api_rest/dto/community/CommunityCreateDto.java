package fr.univartois.butinfo.s5.api_rest.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO (entry) for creating a new community.
 */
public record CommunityCreateDto(
        @NotBlank(message = "The name must not be blank")
        @Size(min = 3, max = 100)
        String name,

        @Size(max = 500)
        String description,
        
        String avatarUrl,

        List<String> topics
) {
}