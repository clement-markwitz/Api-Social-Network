package fr.univartois.butinfo.s5.api_rest.dto.stat;

public record StatsDto(
        long totalUsers,
        long totalPosts,
        long totalCommunities,
        long totalMessages
) {}