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

@Service
public class PageService {

    private final PageRepository pageRepository;
    private final PageMapper pageMapper;

    public PageService(PageRepository pageRepository, PageMapper pageMapper) {
        this.pageRepository = pageRepository;
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
}