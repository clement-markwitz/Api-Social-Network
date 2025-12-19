package fr.univartois.butinfo.s5.api_rest.dto.error;

import java.time.LocalDateTime;

/**
 * DTO for error responses.
 *
 * @param timestamp The time the error occurred
 * @param status The HTTP status code
 * @param error The error description
 * @param message The detailed error message
 * @param path The request path that caused the error
 */
public record ErrorDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {}