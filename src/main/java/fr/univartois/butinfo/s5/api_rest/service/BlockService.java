package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Block;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.BlockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlockService {

    private final BlockRepository blockRepository;

    public BlockService(BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
    }

    /**
     * Enregistre un blocage (reçoit une Entité complète).
     */
    public Block createBlock(Block block, User blocker, User blocked) {

        if (blocker.getId().equals(blocked.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vous ne pouvez pas vous bloquer vous-même.");
        }
        if (blockRepository.existsByBlockerIdAndBlockedId(blocker.getId(), blocked.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà bloqué cet utilisateur.");
        }

        block.setBlocker(blocker);
        block.setBlocked(blocked);
        block.setCreatedAt(LocalDateTime.now());

        return blockRepository.save(block);
    }

    /**
     * Supprime un blocage.
     */
    public void deleteBlock(String blockerId, String blockedId) {
        if (!blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cet utilisateur n'est pas bloqué.");
        }
        blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    /**
     * Récupère les blocages (renvoie des Entités).
     */
    public List<Block> findBlocksByBlocker(String blockerId) {
        return blockRepository.findAllByBlockerId(blockerId);
    }

    public Block getBlockById(String id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocage introuvable"));
    }

    public Block getBlockByBlockerAndBlocked(String blockerId, String blockedId) {
        return blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocage introuvable"));
    }
}