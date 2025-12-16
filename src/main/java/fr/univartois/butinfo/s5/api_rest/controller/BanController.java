package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.ban.BanCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanDto;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.BanService;
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
    public List<BanDto> getAllBans() {
        return banService.getAllBans();
    }

    @PostMapping("/users/{id}/ban")
    public ResponseEntity<BanDto> banUser(
            @PathVariable String id,
            @Valid @RequestBody BanCreateDto dto,
            Authentication authentication) {

        User admin = (User) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(banService.banUser(id, dto, admin.getId()));
    }

    @PostMapping("/users/{id}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable String id) {
        banService.unbanUser(id);
        return ResponseEntity.ok("Utilisateur débanni avec succès");
    }
}
