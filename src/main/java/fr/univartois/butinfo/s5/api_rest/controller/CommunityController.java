package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityDetailDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunitySummaryDto;
import fr.univartois.butinfo.s5.api_rest.dto.community.CommunityUpdateDto;
import fr.univartois.butinfo.s5.api_rest.mapper.CommunityMapper;
import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.CommunityService;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

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
     * Récupérer toutes les communautés (Format Résumé).
     */
    @GetMapping
    public ResponseEntity<List<CommunitySummaryDto>> getAllCommunities() {
        List<CommunitySummaryDto> summaries = communityService.getAll().stream()
                .map(communityMapper::toSummaryDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(summaries);
    }

    /**
     * Récupérer une communauté par son ID (Format Détail).
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommunityDetailDto> getCommunity(@PathVariable String id) {
        Community community = communityService.getById(id); // Attention: Assure-toi que ton Service utilise String
        if (community == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(communityMapper.toDetailDto(community));
    }

    /**
     * Créer une nouvelle communauté.
     */
    @PostMapping
    public ResponseEntity<CommunityDetailDto> createCommunity(@Valid @RequestBody CommunityCreateDto createDto, Authentication authentication) {
        // Conversion DTO -> Entité
        Community entity = communityMapper.toEntity(createDto);

        // Récupération de l'utilisateur connecté pour le définir comme admin
        User admin = (User) authentication.getPrincipal();
        entity.addAdmin(admin);

        // Sauvegarde via le Service
        Community savedCommunity = communityService.createCommunity(entity);

        // Conversion Entité -> DTO Détail pour la réponse
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityMapper.toDetailDto(savedCommunity));
    }

    /**
     * Mettre à jour une communauté.
     */
    @PutMapping("/{id}")
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
     * Supprimer une communauté.
     */
    @DeleteMapping("/{id}")
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