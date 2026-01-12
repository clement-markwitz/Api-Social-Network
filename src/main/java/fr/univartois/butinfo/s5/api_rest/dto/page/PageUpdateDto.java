package fr.univartois.butinfo.s5.api_rest.dto.page;

import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for updating page information.
 */
public record PageUpdateDto(
        @Size(min = 3, max = 100)
        String name,

        @Size(max = 500)
        String description,

        List<String> topics,

        String avatarUrl
) {
}