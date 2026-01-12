package fr.univartois.butinfo.s5.api_rest.dto.page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO (entry) for creating a new page.
 */
public record PageCreateDto(
        @NotBlank(message = "The page name must not be blank")
        @Size(min = 3, max = 100)
        String name,

        @Size(max = 500)
        String description,

        List<String> topics,

        String avatarUrl
) {
}