package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunitySummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityUpdateDto;
import fr.univartois.butinfo.s5.api_rest.mapper.CommunityMapper;
import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing communities.
 */
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityMapper communityMapper;

    public CommunityController(CommunityService communityService, CommunityMapper communityMapper) {
        this.communityService = communityService;
        this.communityMapper = communityMapper;
    }

    /**
     * List all the communities (Summary Format).
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
     * Retrieve a community by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a community by ID", description = "Retrieves details of a community specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    public ResponseEntity<CommunityDetailDto> getCommunity(@PathVariable String id) {
        Community community = communityService.getById(id);
        if (community == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(communityMapper.toDetailDto(community));
    }

    /**
     * Create a new community.
     */
    @PostMapping
    @Operation(summary = "Create a new community", description = "Creates a new community.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Community created successfully")
    })
    public ResponseEntity<CommunityDetailDto> createCommunity(@Valid @RequestBody CommunityCreateDto createDto, Authentication authentication) {
        // Conversion DTO -> Entité
        Community entity = communityMapper.toEntity(createDto);

        User admin = (User) authentication.getPrincipal();
        entity.addAdmin(admin);

        // Sauvegarde via le Service
        Community savedCommunity = communityService.createCommunity(entity);

        // Conversion Entité -> DTO Détail pour la réponse
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityMapper.toDetailDto(savedCommunity));
    }

    /**
     * Update an existing community.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a community", description = "Updates a community specified by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (You are not an admin of this community)"),
            @ApiResponse(responseCode = "404", description = "Community not found")
    })
    public ResponseEntity<CommunityDetailDto> updateCommunity(
            @PathVariable String id,
            @Valid @RequestBody CommunityUpdateDto updateDto,
            Authentication authentication) {

        Community existingCommunity = communityService.getById(id);
        if (existingCommunity == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Vérification des droits
        User currentUser = (User) authentication.getPrincipal();
        communityService.checkAdminRights(existingCommunity, currentUser);

        // 2. Mise à jour
        communityMapper.updateEntityFromDto(updateDto, existingCommunity);
        Community updatedCommunity = communityService.updateCommunity(existingCommunity);

        return ResponseEntity.ok(communityMapper.toDetailDto(updatedCommunity));
    }

    /**
     * Delete a community.
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
        if (existingCommunity == null) {
            return ResponseEntity.notFound().build();
        }

        // 1. Vérification des droits
        User currentUser = (User) authentication.getPrincipal();
        communityService.checkAdminRights(existingCommunity, currentUser);

        // 2. Suppression
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }
}