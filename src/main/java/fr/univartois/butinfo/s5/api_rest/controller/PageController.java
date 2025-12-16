package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.page.PageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageUpdateDto;
import fr.univartois.butinfo.s5.api_rest.service.PageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/pages")
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping
    public Page<PageSummaryDto> getAllPages(@PageableDefault(size = 20) Pageable pageable) {
        return pageService.getAllPages(pageable);
    }

    @GetMapping("/{id}")
    public PageDetailDto getPage(@PathVariable String id) {
        return pageService.getPageById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PageDetailDto createPage(@RequestBody @Valid PageCreateDto pageCreateDto) {
        String userId = "6941203e6e49a2111667cb3c";
        return pageService.createPage(pageCreateDto, userId);
    }

    @PutMapping("/{id}")
    public PageDetailDto updatePage(@PathVariable String id, @RequestBody @Valid PageUpdateDto pageUpdateDto) {
        return pageService.updatePage(id, pageUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePage(@PathVariable String id) {
        pageService.deletePage(id);
    }
}