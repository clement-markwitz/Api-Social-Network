package fr.univartois.butinfo.s5.api_rest.mapper;

import fr.univartois.butinfo.s5.api_rest.dto.page.PageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.model.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PageMapper {

    public PageDetailDto toDetailDto(Page page) {
        if (page == null) return null;

        return new PageDetailDto(
                page.getId(),
                page.getName(),
                page.getDescription(),
                page.getAvatarUrl(),
                page.getAdminIds(), // Correction : getAdminIds() existe maintenant
                page.getFollowerCount(),
                page.getTopics(),
                page.getCreatedAt()
        );
    }

    public PageSummaryDto toSummaryDto(Page page) {
        if (page == null) return null;

        return new PageSummaryDto(
                page.getId(),
                page.getName(),
                page.getAvatarUrl(),
                page.getFollowerCount()
        );
    }

    public Page toEntity(PageCreateDto dto, String creatorUserId) {
        if (dto == null) return null;

        return Page.builder()
                .name(dto.name())
                .description(dto.description())
                .topics(dto.topics())
                .adminIds(List.of(creatorUserId)) // On met le créateur comme premier admin
                .followerCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}