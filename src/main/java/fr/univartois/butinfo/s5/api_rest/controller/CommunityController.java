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
    @Operation(summary = "Lister toutes les communautés", description = "Récupère une liste de toutes les communautés au format résumé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des communautés récupérée avec succès")
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
    @Operation(summary = "Récupérer une communauté par ID", description = "Récupère les détails d'une communauté spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Communauté récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Communauté non trouvée")
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
    @Operation(summary = "Créer une nouvelle communauté", description = "Permet de créer une nouvelle communauté.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Communauté créée avec succès")
    })
    public ResponseEntity<CommunityDetailDto> createCommunity(@Valid @RequestBody CommunityCreateDto createDto, Authentication authentication) {
        // Conversion DTO -> Entité
        Community entity = communityMapper.toEntity(createDto);

        // CORRECTION TODO : La récupération du user est implémentée ici, plus de commentaire TODO.
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
    @Operation(summary = "Mettre à jour une communauté", description = "Permet de mettre à jour une communauté spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Communauté mise à jour avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Vous n'êtes pas admin de cette communauté)"),
            @ApiResponse(responseCode = "404", description = "Communauté non trouvée")
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
    @Operation(summary = "Supprimer une communauté", description = "Permet de supprimer une communauté spécifiée par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Communauté supprimée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Vous n'êtes pas admin de cette communauté)"),
            @ApiResponse(responseCode = "404", description = "Communauté non trouvée")
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