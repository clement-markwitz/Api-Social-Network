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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {

    private final BlockService blockService;
    private final UserService userService; // Nécessaire pour récupérer l'user à bloquer
    private final BlockMapper blockMapper;
    private final UserMapper userMapper;

    public BlockController(BlockService blockService, UserService userService, BlockMapper blockMapper, UserMapper userMapper) {
        this.blockService = blockService;
        this.userService = userService;
        this.blockMapper = blockMapper;
        this.userMapper = userMapper;
    }

    /**
     * Bloquer un utilisateur.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Void> blockUser(
            @PathVariable String userId,
            @RequestBody(required = false) BlockCreateDto dto,
            Authentication authentication) {

        // 1. Préparer le DTO (gestion du null)
        BlockCreateDto createDto = (dto != null) ? dto : new BlockCreateDto(null);

        // 2. Conversion DTO -> Entité
        Block block = blockMapper.toEntity(createDto);

        // 3. Récupération des Users et Hydratation de l'Entité
        User blocker = (User) authentication.getPrincipal();
        User blocked = userService.getById(userId); // UserService lance 404 si pas trouvé

        block.setBlocker(blocker);
        block.setBlocked(blocked);
        block.setCreatedAt(LocalDateTime.now());

        // 4. Appel du Service
        blockService.createBlock(block);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Débloquer un utilisateur.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unblockUser(@PathVariable String userId, Authentication authentication) {
        User blocker = (User) authentication.getPrincipal();

        // Ici on passe juste les IDs car la suppression n'a pas besoin de l'objet User complet
        blockService.deleteBlock(blocker.getId(), userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getMyBlockedUsersSummary(Authentication authentication) {
        User blocker = (User) authentication.getPrincipal();
        List<Block> blocks = blockService.findBlocksByBlocker(blocker.getId());

        // On extrait l'utilisateur 'blocked' de chaque blocage et on le mappe
        List<UserSummaryDto> userSummaries = blocks.stream()
                .map(block -> userMapper.toSummaryDto(block.getBlocked()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userSummaries);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BlockUserDto> getBlockByIdBlocker(@PathVariable String userId, Authentication authentication) {
        Block block = blockService.getBlockByBlockerAndBlocked(
                ((User) authentication.getPrincipal()).getId(),
                userId
        );

        if (block == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(blockMapper.toDto(block));
    }
}