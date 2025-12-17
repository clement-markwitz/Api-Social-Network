package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.ban.BanCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.BanMapper;
import fr.univartois.butinfo.s5.api_rest.model.Ban;
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
    private final BanMapper banMapper;

    public BanController(BanService banService, BanMapper banMapper) {
        this.banService = banService;
        this.banMapper = banMapper;
    }

    @GetMapping("/bans")
    @Operation(summary = "Get all bans", description = "Retrieve a list of all bans.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bans retrieved successfully")
    })
    public List<BanSummaryDto> getAllBans() {

        return banService.getAllBans().stream()
                .map(banMapper::toSummaryDto)
                .toList();
    }

    @PostMapping("/users/{idUser}/ban")
    @Operation(summary = "Ban a user", description = "Ban a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully banned"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ban data"),
    })
    public ResponseEntity<BanDto> banUser(
            @PathVariable String idUser,
            @Valid @RequestBody BanCreateDto dto,
            Authentication authentication) {

        User admin = (User) authentication.getPrincipal();
        Ban ban = banMapper.toEntity(dto);
        Ban savedBan = banService.banUser(idUser, ban, admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(banMapper.toDto(savedBan));
    }

    @PostMapping("/users/{idUser}/unban")
    @Operation(summary = "Unban a user", description = "Unban a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully unbanned"),
            @ApiResponse(responseCode = "404", description = "User not found or not banned")
    })
    public ResponseEntity<String> unbanUser(@PathVariable String idUser) {
        banService.unbanUser(idUser);
        return ResponseEntity.ok("Utilisateur débanni avec succès");
    }
}
