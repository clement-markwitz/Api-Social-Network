package fr.univartois.butinfo.s5.api_rest.dto.stat;

/**
 * DTO (output) for platform statistics.
 *
 * @param totalUsers The total number of users.
 * @param totalPosts The total number of posts.
 * @param totalCommunities The total number of communities.
 * @param totalMessages The total number of messages.
 */
public record StatsDto(
        long totalUsers,
        long totalPosts,
        long totalCommunities,
        long totalMessages
) {}