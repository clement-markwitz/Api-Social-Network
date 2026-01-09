package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunitySummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityUpdateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.mapper.CommunityMapper;
import fr.univartois.butinfo.s5.api_rest.mapper.PostMapper;
import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.CommunityService;
import fr.univartois.butinfo.s5.api_rest.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for managing communities.
 */
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityMapper communityMapper;
    private final PostService postService;
    private final PostMapper postMapper;

    /**
     * Constructor for CommunityController.
     *
     * @param communityService the community service
     * @param communityMapper  the community mapper
     * @param postService      the post service
     * @param postMapper       the post mapper
     */
    public CommunityController(CommunityService communityService, CommunityMapper communityMapper,
            PostService postService, PostMapper postMapper) {
        this.communityService = communityService;
        this.communityMapper = communityMapper;
        this.postService = postService;
        this.postMapper = postMapper;
    }

    /**
     * Get all communities.
     *
     * @return List of CommunitySummaryDto
     */
    @GetMapping
    @Operation(summary = "List all communities", description = "Retrieves a list of all communities in summary format.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Communities list retrieved successfully")
    })
    public ResponseEntity<List<CommunitySummaryDto>> getAllCommunities() {
        List<CommunitySummaryDto> summaries = communityService.getAll().stream()
                .map(communityMapper::toSummaryDto)
                .toList();
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get a community by ID.
     *
     * @param id Community ID
     * @return CommunityDetailDto
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a community by ID", description = "Retrieves details of a community specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    public ResponseEntity<CommunityDetailDto> getCommunity(@PathVariable String id) {
        Community community = communityService.getById(id);
        List<User> members = communityService.getMembersByCommunityId(id);
        return ResponseEntity.ok(communityMapper.toDetailDto(community, members));
    }

    /**
     * Create a new community.
     *
     * @param createDto      CommunityCreateDto
     * @param authentication Authentication
     * @return ResponseEntity with CommunityDetailDto
     */
    @PostMapping
    @Operation(summary = "Create a new community", description = "Creates a new community.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Community created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid community data")
    })
    public ResponseEntity<CommunityDetailDto> createCommunity(@Valid @RequestBody CommunityCreateDto createDto,
            Authentication authentication) {

        Community entity = communityMapper.toEntity(createDto);
        User admin = (User) authentication.getPrincipal();
        entity.addAdmin(admin);
        Community savedCommunity = communityService.createCommunity(entity);
        List<User> members = communityService.getMembersByCommunityId(savedCommunity.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityMapper.toDetailDto(savedCommunity, members));
    }

    /**
     * Update a community.
     *
     * @param id             Community ID
     * @param updateDto      CommunityUpdateDto
     * @param authentication Authentication
     * @return ResponseEntity with CommunityDetailDto
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a community", description = "Updates a community specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (You are not an admin of this community)"),
            @ApiResponse(responseCode = "404", description = "Community not found"),
            @ApiResponse(responseCode = "400", description = "Invalid community data")
    })
    public ResponseEntity<CommunityDetailDto> updateCommunity(
            @PathVariable String id,
            @Valid @RequestBody CommunityUpdateDto updateDto,
            Authentication authentication) {

        Community existingCommunity = communityService.getById(id);
        User currentUser = (User) authentication.getPrincipal();
        boolean isAdmin = communityService.isCommunityAdmin(existingCommunity, currentUser);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not an admin of this community");
        }
        communityMapper.updateEntityFromDto(updateDto, existingCommunity);
        Community updatedCommunity = communityService.updateCommunity(existingCommunity);
        List<User> members = communityService.getMembersByCommunityId(id);

        return ResponseEntity.ok(communityMapper.toDetailDto(updatedCommunity, members));
    }

    /**
     * Delete a community.
     *
     * @param id             Community ID
     * @param authentication Authentication
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a community", description = "Deletes a community specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Community deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (You are not an admin of this community)"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    public ResponseEntity<Void> deleteCommunity(@PathVariable String id, Authentication authentication) {

        Community existingCommunity = communityService.getById(id);
        User currentUser = (User) authentication.getPrincipal();
        boolean isAdmin = communityService.isCommunityAdmin(existingCommunity, currentUser);
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not an admin of this community");
        }
        communityService.deleteCommunity(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get posts of a community.
     *
     * @param id Community ID
     * @return List of PostDto
     */
    @Operation(summary = "Get posts of a community", description = "Retrieves all posts associated with a specific community.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDto>> getCommunityPosts(@PathVariable String id) {
        List<Post> posts = postService.getPostsByCommunity(id);
        List<PostDto> postDtos = posts.stream()
                .map(postMapper::toDto)
                .toList();
        return ResponseEntity.ok(postDtos);
    }

    /**
     * Follow a community.
     * 
     * @param id Community ID
     * @return
     */
    @Operation(summary = "Follow a community", description = "Allows the authenticated user to follow a specific community.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully followed the community"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    @PostMapping("/{id}/follow")
    public ResponseEntity<Void> followCommunity(@PathVariable String id, Authentication authentication) {
        Community community = communityService.getById(id);
        User user = (User) authentication.getPrincipal();
        communityService.addMemberToCommunity(id, user);
        community.setMemberCount(community.getMemberCount() + 1);
        communityService.updateCommunity(community);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Unfollow a community.
     * 
     * @param id Community ID
     * @return
     */
    @Operation(summary = "Unfollow a community", description = "Allows the authenticated user to unfollow a specific community.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully unfollowed the community"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<Void> unfollowCommunity(@PathVariable String id, Authentication authentication) {
        Community community = communityService.getById(id);
        User user = (User) authentication.getPrincipal();
        communityService.removeMemberFromCommunity(id, user.getId());
        community.setMemberCount(community.getMemberCount() - 1);
        communityService.updateCommunity(community);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}