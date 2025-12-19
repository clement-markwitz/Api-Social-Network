package fr.univartois.butinfo.s5.api_rest.dto.stat;

/**
 * DTO (output) for user statistics.
 *
 * @param totalUsers the total number of users
 */
public record StatsDtoUser(
        long totalUsers
) {
}
