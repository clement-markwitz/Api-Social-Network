package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Block;
import fr.univartois.butinfo.s5.api_rest.model.Conversation;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.BlockRepository;
import fr.univartois.butinfo.s5.api_rest.repository.ConversationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing user blocks.
 */
@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final ConversationRepository conversationRepository;

    public BlockService(BlockRepository blockRepository, ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
        this.blockRepository = blockRepository;
    }

    /**
     * Create a new block between two users.
     * @param block the block entity
     * @param blocker the user who is blocking
     * @param blocked the user being blocked
     * @return the created block
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

        Conversation conversation = conversationRepository
                .findByMembersIdsContainsAll(List.of(blocker.getId(), blocked.getId()))
                .orElse(null);

        if (conversation != null) {
            conversationRepository.delete(conversation);
        }

        return blockRepository.save(block);
    }

    /**
     * Delete a block between two users.
     *
     * @param blockerId the ID of the user who initiated the block
     * @param blockedId the ID of the user being blocked
     */
    public void deleteBlock(String blockerId, String blockedId) {
        if (!blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cet utilisateur n'est pas bloqué.");
        }
        blockRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
    }

    /**
     * Find all blocks initiated by a specific user.
     *
     * @param blockerId the ID of the user who initiated the blocks
     * @return a list of blocks
     */
    public List<Block> findBlocksByBlocker(String blockerId) {
        return blockRepository.findAllByBlockerId(blockerId);
    }

    /**
     * Get all blocks received by a specific user.
     */
    public List<Block> findBlocksByBlocked(String blockedId) {
        return blockRepository.findAllByBlockedId(blockedId);
    }

    /**
     * Get a block by its ID.
     *
     * @param id the ID of the block
     * @return the block
     */
    public Block getBlockById(String id) {
        return blockRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocage introuvable"));
    }

    /**
     * Get a block by blocker and blocked user IDs.
     *
     * @param blockerId the ID of the user who initiated the block
     * @param blockedId the ID of the user being blocked
     * @return the block
     */
    public Block getBlockByBlockerAndBlocked(String blockerId, String blockedId) {
        return blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blocage introuvable"));
    }
}