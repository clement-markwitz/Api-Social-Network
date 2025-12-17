package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.page.PageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageUpdateDto;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.PageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pages")
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    /**
     * Get all pages with pagination.
     *
     * @param pageable the pagination information
     * @return a page of PageSummaryDto
     */
    @GetMapping
    @Operation(summary = "Lister toutes les pages", description = "Récupère une liste paginée de toutes les pages au format résumé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des pages récupérée avec succès")
    })
    public Page<PageSummaryDto> getAllPages(@PageableDefault(size = 20) Pageable pageable) {
        return pageService.getAllPages(pageable);
    }

    /**
     * Get a page by its ID.
     *
     * @param id the ID of the page
     * @return the PageDetailDto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une page par ID", description = "Récupère les détails d'une page spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Page non trouvée")
    })
    public PageDetailDto getPage(@PathVariable String id) {
        return pageService.getPageById(id);
    }

    /**
     * Create a new page.
     *
     * @param pageCreateDto the page creation data
     * @param authentication the authentication object
     * @return the created PageDetailDto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer une nouvelle page", description = "Permet de créer une nouvelle page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Page créée avec succès")
    })
    public PageDetailDto createPage(@RequestBody @Valid PageCreateDto pageCreateDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return pageService.createPage(pageCreateDto, user.getId());
    }

    /**
     * Update an existing page.
     *
     * @param id the ID of the page to update
     * @param pageUpdateDto the page update data
     * @return the updated PageDetailDto
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une page", description = "Permet de mettre à jour une page existante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Page non trouvée")
    })
    public PageDetailDto updatePage(@PathVariable String id, @RequestBody @Valid PageUpdateDto pageUpdateDto) {
        return pageService.updatePage(id, pageUpdateDto);
    }

    /**
     * Delete a page by its ID.
     *
     * @param id the ID of the page to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Supprimer une page", description = "Permet de supprimer une page spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Page supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Page non trouvée")
    })
    public void deletePage(@PathVariable String id) {
        pageService.deletePage(id);
    }
}