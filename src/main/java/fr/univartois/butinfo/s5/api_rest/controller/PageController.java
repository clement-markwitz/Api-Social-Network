package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.page.PageCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageSummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.page.PageUpdateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.mapper.PostMapper;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.PageService;
import fr.univartois.butinfo.s5.api_rest.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing pages.
 */
@RestController
@RequestMapping("/api/pages")
public class PageController {

    private final PageService pageService;
    private final PostService postService;
    private final PostMapper postMapper;

    public PageController(PageService pageService, PostService postService, PostMapper postMapper) {
        this.pageService = pageService;
        this.postService = postService;
        this.postMapper = postMapper;
    }

    /**
     * Get all pages with pagination.
     *
     * @param pageable the pagination information
     * @return a page of PageSummaryDto
     */
    @GetMapping
    @Operation(summary = "List all pages", description = "Retrieves a paginated list of all pages in summary format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pages list retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
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
    @Operation(summary = "Get a page by ID", description = "Retrieves details of a page specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Page not found")
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
    @Operation(summary = "Create a new page", description = "Creates a new page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Page created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
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
    @Operation(summary = "Update a page", description = "Updates an existing page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page updated successfully"),
            @ApiResponse(responseCode = "404", description = "Page not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
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
    @Operation(summary = "Delete a page", description = "Deletes a page specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Page deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Page not found")
    })
    public void deletePage(@PathVariable String id) {
        pageService.deletePage(id);
    }

    /**
     * Get all posts associated with a specific page.
     *
     * @param id the ID of the page
     * @return a list of PostDto
     */
    @Operation(summary = "Get posts of a page", description = "Retrieves all posts associated with a specific page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Page not found")
    })
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDto>> getPagePosts(@PathVariable String id) {
        List<Post> posts = postService.getPostsByPage(id);
        List<PostDto> postDtos = posts.stream()
                .map(postMapper::toDto)
                .toList();
        return ResponseEntity.ok(postDtos);
    }

    /**
     * Follow a page.
     *
     * @param id the ID of the page to follow
     * @param authentication the authentication object
     * @return ResponseEntity with status OK
     */
    @Operation(summary = "Follow a page", description = "Allows the authenticated user to follow a specific page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully followed the page"),
            @ApiResponse(responseCode = "404", description = "Page not found"),
    })
    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followPage(@PathVariable String id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        pageService.followPage(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Unfollow a page.
     *
     * @param id the ID of the page to unfollow
     * @param authentication the authentication object
     * @return ResponseEntity with status OK
     */
    @Operation(summary = "Unfollow a page", description = "Allows the authenticated user to unfollow a specific page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully unfollowed the page"),
            @ApiResponse(responseCode = "404", description = "Page not found")
    })
    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollowPage(@PathVariable String id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        pageService.unfollowPage(id, user);
        return ResponseEntity.ok().build();
    }

}