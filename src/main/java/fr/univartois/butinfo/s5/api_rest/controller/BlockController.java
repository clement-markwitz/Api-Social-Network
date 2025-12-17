package fr.univartois.butinfo.s5.api_rest.controller;

import fr.univartois.butinfo.s5.api_rest.dto.block.BlockCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.block.BlockUserDto;
import fr.univartois.butinfo.s5.api_rest.dto.user.UserSummaryDto;
import fr.univartois.butinfo.s5.api_rest.mapper.BlockMapper;
import fr.univartois.butinfo.s5.api_rest.mapper.UserMapper;
import fr.univartois.butinfo.s5.api_rest.model.Block;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.service.BlockService;
import fr.univartois.butinfo.s5.api_rest.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BlockController {

    private final BlockService blockService;
    private final UserService userService;
    private final BlockMapper blockMapper;

    public BlockController(BlockService blockService, UserService userService, BlockMapper blockMapper) {
        this.blockService = blockService;
        this.userService = userService;
        this.blockMapper = blockMapper;
    }

    /**
     * Bloquer un utilisateur.
     */
    @PostMapping("/users/{userId}/blocks")
    @Operation(summary = "Bloquer un utilisateur", description = "Permet de bloquer un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur bloqué avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<Block> blockUser(
            @PathVariable String userId,
            @RequestBody(required = false) BlockCreateDto dto,
            Authentication authentication) {

        BlockCreateDto createDto = (dto != null) ? dto : new BlockCreateDto(null);
        Block block = blockMapper.toEntity(createDto);
        User blocker = (User) authentication.getPrincipal();
        User blocked = userService.getById(userId);

        // 4. Appel du Service
        Block createdBlock = blockService.createBlock(block, blocker, blocked);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlock);
    }

    /**
     * Débloquer un utilisateur.
     */
    @DeleteMapping("/users/{userId}/blocks")
    @Operation(summary = "Débloquer un utilisateur", description = "Permet de débloquer un utilisateur spécifié par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur débloqué avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé ou non bloqué")
    })
    public ResponseEntity<Void> unblockUser(@PathVariable String userId, Authentication authentication) {
        User blocker = (User) authentication.getPrincipal();

        // Ici on passe juste les IDs car la suppression n'a pas besoin de l'objet User complet
        blockService.deleteBlock(blocker.getId(), userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/blocks")
    @Operation(summary = "Lister les utilisateurs bloqués", description = "Récupère la liste des utilisateurs que l'utilisateur authentifié a bloqués.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<List<BlockUserDto>> getMyBlockedUsers(Authentication authentication) {
        User blocker = (User) authentication.getPrincipal();
        List<Block> blocks = blockService.findBlocksByBlocker(blocker.getId());

        List<BlockUserDto> userSummaries = blocks.stream()
                .map(blockMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userSummaries);
    }

    @GetMapping("/users/{id}/blocks")
    @Operation(summary = "Récupérer un blocage par ID", description = "Récupère les détails d'un blocage spécifique par son ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Blocage récupéré avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (le blocage n'appartient pas à l'utilisateur)"),
            @ApiResponse(responseCode = "404", description = "Blocage non trouvé")
    })
    public ResponseEntity<BlockUserDto> getBlockById(@PathVariable String id, Authentication authentication) {
        User blocker = (User) authentication.getPrincipal();
        Block block = blockService.getBlockById(id);
        if (block == null) {
            return ResponseEntity.notFound().build();
        }
        if (!block.getBlocker().getId().equals(blocker.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(blockMapper.toDto(block));
    }
}