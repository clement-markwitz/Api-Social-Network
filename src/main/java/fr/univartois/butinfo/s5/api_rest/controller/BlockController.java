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
    @PostMapping("/{userId}")
    @Operation(summary = "Block a user", description = "Blocks a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully blocked"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Block> blockUser(
            @PathVariable String userId,
            @RequestBody(required = false) BlockCreateDto dto,
            Authentication authentication) {

        // 1. Préparer le DTO (gestion du null)
        BlockCreateDto createDto = (dto != null) ? dto : new BlockCreateDto(null);

        // 2. Conversion DTO -> Entité
        Block block = blockMapper.toEntity(createDto);

        // 3. Récupération des Users et Hydratation de l'Entité
        User blocker = (User) authentication.getPrincipal();
        User blocked = userService.getById(userId);

        // 4. Appel du Service
        Block createdBlock = blockService.createBlock(block, blocker, blocked);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlock);
    }

    /**
     * Débloquer un utilisateur.
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "Unblock a user", description = "Allows unblocking a user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully unblocked"),
            @ApiResponse(responseCode = "404", description = "User not found or not blocked")
    })
    public ResponseEntity<Void> unblockUser(@PathVariable String userId, Authentication authentication) {
        User blocker = (User) authentication.getPrincipal();

        // Ici on passe juste les IDs car la suppression n'a pas besoin de l'objet User complet
        blockService.deleteBlock(blocker.getId(), userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List blocked users", description = "Retrieves the list of users that the authenticated user has blocked.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated")
    })
    public ResponseEntity<List<BlockUserDto>> getMyBlockedUsers(Authentication authentication) {
        User blocker = (User) authentication.getPrincipal();
        List<Block> blocks = blockService.findBlocksByBlocker(blocker.getId());

        List<BlockUserDto> userSummaries = blocks.stream()
                .map(blockMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userSummaries);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a block by ID", description = "Retrieves details of a specific block by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Block retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (the block does not belong to the user)"),
            @ApiResponse(responseCode = "404", description = "Block not found")
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