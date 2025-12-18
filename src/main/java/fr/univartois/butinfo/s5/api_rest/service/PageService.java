package fr.univartois.butinfo.s5.api_rest.service;
import fr.univartois.butinfo.s5.api_rest.dto.page.*;
import fr.univartois.butinfo.s5.api_rest.mapper.PageMapper;
import fr.univartois.butinfo.s5.api_rest.model.Page;
import fr.univartois.butinfo.s5.api_rest.repository.PageRepository;
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

    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

    public PageService(PageRepository pageRepository, PageMapper pageMapper) {
        this.pageRepository = pageRepository;
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
        if (pageRepository.existsByName(dto.name())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Une page avec ce nom existe déjà.");
        }
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page introuvable"));
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Page introuvable"));
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page introuvable");
        }
        pageRepository.deleteById(id);
    }
}