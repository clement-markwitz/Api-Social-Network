package fr.univartois.butinfo.s5.api_rest.service;
import fr.univartois.butinfo.s5.api_rest.dto.page.*;
import fr.univartois.butinfo.s5.api_rest.mapper.PageMapper;
import fr.univartois.butinfo.s5.api_rest.model.Page;
import fr.univartois.butinfo.s5.api_rest.model.PageSubscription;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.PageRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PageSubscriptionRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@Service
public class PageService {

    private final PageRepository pageRepository;
    private final PageSubscriptionRepository pageSubscriptionRepository;
    private final PageMapper pageMapper;

    public PageService(PageRepository pageRepository, PageSubscriptionRepository pageSubscriptionRepository, PageMapper pageMapper) {
        this.pageRepository = pageRepository;
        this.pageSubscriptionRepository = pageSubscriptionRepository;
        this.pageMapper = pageMapper;
    }

    public PageDetailDto createPage(PageCreateDto dto, String userId) {
        if (pageRepository.existsByName(dto.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une page avec ce nom existe déjà.");
        }
        Page page = pageMapper.toEntity(dto, userId);

        Page savedPage = pageRepository.save(page);
        return pageMapper.toDetailDto(savedPage);
    }

    public org.springframework.data.domain.Page<PageSummaryDto> getAllPages(Pageable pageable) {
        return pageRepository.findAll(pageable)
                .map(pageMapper::toSummaryDto);
    }

    public PageDetailDto getPageById(String id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page introuvable"));
        return pageMapper.toDetailDto(page);
    }

    public PageDetailDto updatePage(String id, PageUpdateDto dto) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page introuvable"));
        if (dto.description() != null) page.setDescription(dto.description());
        if (dto.topics() != null) page.setTopics(dto.topics());

        page.setUpdatedAt(LocalDateTime.now());

        Page updatedPage = pageRepository.save(page);
        return pageMapper.toDetailDto(updatedPage);
    }

    public void deletePage(String id) {
        if (!pageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page introuvable");
        }
        pageRepository.deleteById(id);
    }

    public void followPage(String pageId, User user) {
        Page page = findPageEntityById(pageId);
        if (pageSubscriptionRepository.existsByUserAndPage(user, page)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous suivez déjà cette page.");
        }
        PageSubscription subscription = new PageSubscription();
        subscription.setUser(user);
        subscription.setPage(page);
        subscription.setCreatedAt(LocalDateTime.now());
        pageSubscriptionRepository.save(subscription);
        page.setFollowerCount(page.getFollowerCount() + 1);
        pageRepository.save(page);
    }

    public void unfollowPage(String pageId, User user) {
        Page page = findPageEntityById(pageId);
        PageSubscription subscription = pageSubscriptionRepository.findByUserAndPage(user, page)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vous ne suivez pas cette page."));
        pageSubscriptionRepository.delete(subscription);
        page.setFollowerCount(Math.max(0, page.getFollowerCount() - 1));
        pageRepository.save(page);
    }

    private Page findPageEntityById(String id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page introuvable"));
    }
}