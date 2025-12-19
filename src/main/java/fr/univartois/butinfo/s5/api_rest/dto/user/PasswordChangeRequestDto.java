package fr.univartois.butinfo.s5.api_rest.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (entry point) for changing a user's password.
 */
public record PasswordChangeRequestDto(
    @NotBlank
    String oldPassword,

    @NotBlank
    @Size(min = 8, max = 100)
    String newPassword
) {}