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

/**
 * Service class for managing Page entities.
 */
@Service
public class PageService {

    private static final String PAGE_NOT_FOUND = "Page introuvable";

    private final PageRepository pageRepository;
    private final PageSubscriptionRepository pageSubscriptionRepository;
    private final PageMapper pageMapper;

    /**
     * Constructor for PageService.
     *
     * @param pageRepository the page repository
     * @param pageMapper    the page mapper
     * @param pageSubscriptionRepository the page subscription repository
     */
    public PageService(PageRepository pageRepository, PageMapper pageMapper, PageSubscriptionRepository pageSubscriptionRepository) {
        this.pageRepository = pageRepository;
        this.pageSubscriptionRepository = pageSubscriptionRepository;
        this.pageMapper = pageMapper;
    }

    /**
     * Creates a new page.
     *
     * @param dto    the data transfer object containing page creation data
     * @param userId the ID of the user creating the page
     * @return the detailed DTO of the created page
     * @throws ResponseStatusException if a page with the same name already exists
     */
    public PageDetailDto createPage(PageCreateDto dto, String userId) {
        Page page = pageMapper.toEntity(dto, userId);

        Page savedPage = pageRepository.save(page);
        return pageMapper.toDetailDto(savedPage);
    }

    /**
     * Retrieves all pages with pagination.
     *
     * @param pageable pagination information
     * @return a page of summary DTOs of all pages
     */
    public org.springframework.data.domain.Page<PageSummaryDto> getAllPages(Pageable pageable) {
        return pageRepository.findAll(pageable)
                .map(pageMapper::toSummaryDto);
    }

    /**
     * Retrieves a page by its ID.
     *
     * @param id the ID of the page
     * @return the detailed DTO of the page
     * @throws ResponseStatusException if the page is not found
     */
    public PageDetailDto getPageById(String id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PAGE_NOT_FOUND));
        return pageMapper.toDetailDto(page);
    }
    /**
     * Updates an existing page.
     *
     * @param id  the ID of the page to update
     * @param dto the data transfer object containing page update data
     * @return the detailed DTO of the updated page
     * @throws ResponseStatusException if the page is not found
     */
    public PageDetailDto updatePage(String id, PageUpdateDto dto) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PAGE_NOT_FOUND));
        if (dto.description() != null) page.setDescription(dto.description());
        if (dto.topics() != null) page.setTopics(dto.topics());

        page.setUpdatedAt(LocalDateTime.now());

        Page updatedPage = pageRepository.save(page);
        return pageMapper.toDetailDto(updatedPage);
    }
    /**
     * Deletes a page by its ID.
     *
     * @param id the ID of the page to delete
     * @throws ResponseStatusException if the page is not found
     */
    public void deletePage(String id) {
        if (!pageRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, PAGE_NOT_FOUND);
        }
        pageRepository.deleteById(id);
    }

    /**
     * Follow a page.
     *
     * @param pageId the ID of the page to follow
     * @param user the user who wants to follow the page
     */
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

    /**
     * Unfollow a page.
     *
     * @param pageId the ID of the page to unfollow
     * @param user the user who wants to unfollow the page
     */
    public void unfollowPage(String pageId, User user) {
        Page page = findPageEntityById(pageId);
        PageSubscription subscription = pageSubscriptionRepository.findByUserAndPage(user, page)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vous ne suivez pas cette page."));
        pageSubscriptionRepository.delete(subscription);
        page.setFollowerCount(Math.max(0, page.getFollowerCount() - 1));
        pageRepository.save(page);
    }

    /**
     * Finds a Page entity by its ID.
     *
     * @param id the ID of the page
     * @return the Page entity
     */
    private Page findPageEntityById(String id) {
        return pageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PAGE_NOT_FOUND));
    }

}