package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.ban.BanCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanDto;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.BanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class BanController {
    private final BanService banService;

    public BanController(BanService banService) {
        this.banService = banService;
    }

    @GetMapping("/bans")
    @Operation(summary = "Lister tous les bannissements", description = "Récupère l'historique complet des utilisateurs bannis.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Vous n'êtes pas ADMIN)")
    })
    public List<BanDto> getAllBans() {
        return banService.getAllBans();
    }

    @PostMapping("/users/{id}/ban")
    @Operation(summary = "Bannir un utilisateur", description = "Bannit un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur banni avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Vous n'êtes pas ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<BanDto> banUser(
            @PathVariable String id,
            @Valid @RequestBody BanCreateDto dto,
            Authentication authentication) {

        User admin = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(banService.banUser(id, dto, admin.getId()));
    }

    @PostMapping("/users/{id}/unban")
    @Operation(summary = "Débannir un utilisateur", description = "Débannit un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur débanni avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (Vous n'êtes pas ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<String> unbanUser(@PathVariable String id) {
        banService.unbanUser(id);
        return ResponseEntity.ok("Utilisateur débanni avec succès");
    }
}
