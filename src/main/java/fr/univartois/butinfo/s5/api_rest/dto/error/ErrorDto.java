package fr.univartois.butinfo.s5.api_rest.dto.error;

import java.time.LocalDateTime;

public record ErrorDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}