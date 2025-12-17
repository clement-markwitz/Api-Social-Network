package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDto;
import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDtoPost;
import fr.univartois.butinfo.s5.api_rest.dto.stat.StatsDtoUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    StatsDto toStatsDto(long totalUsers, long totalPosts, long totalCommunities, long totalMessages);
    
    default StatsDtoUser toStatsDtoUser(long totalUsers) {
        return new StatsDtoUser(totalUsers);
    }

    default StatsDtoPost toStatsDtoPost(long totalPosts) {
        return new StatsDtoPost(totalPosts);
    }
}