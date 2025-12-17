package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.ban.BanCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.ban.BanDto;
import fr.univartois.butinfo.s5.api_rest.mapper.BanMapper;
import fr.univartois.butinfo.s5.api_rest.model.Ban;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.BanRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing user bans.
 */
@Service
public class BanService {
    private final BanRepository banRepository;
    private final UserRepository userRepository;

    public BanService(BanRepository banRepository, UserRepository userRepository) {
        this.banRepository = banRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all bans.
     * @return
     */
    public List<Ban> getAllBans() {
        return banRepository.findAll();
    }

    /**
     * Ban a user.
     * @param targetUserId
     * @param ban
     * @param admin
     * @return
     */
    public Ban banUser(String targetUserId, Ban ban, User admin) {
        // Verify if the user to be banned exists
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        // Verify if the user is already banned
        if (banRepository.findByUserIdAndActiveTrue(targetUserId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'utilisateur est déjà banni");
        }

        ban.setUser(targetUser);
        ban.setModerator(admin);

        ban.setActive(true);
        ban.setStartAt(LocalDateTime.now());
        ban.setEndAt(LocalDateTime.now().plusDays(ban.getDurationDays()));
        ban.setCreatedAt(LocalDateTime.now());
        ban.setUpdatedAt(LocalDateTime.now());

        Ban savedBan = banRepository.save(ban);

        targetUser.setBanned(true);
        userRepository.save(targetUser);

        return savedBan;
    }

    /**
     * Unban a user.
     * @param targetUserId
     */
    public void unbanUser(String targetUserId) {
        Ban activeBan = banRepository.findByUserIdAndActiveTrue(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun bannissement actif trouvé pour cet utilisateur"));

        activeBan.setActive(false);
        activeBan.setEndAt(LocalDateTime.now());
        banRepository.save(activeBan);

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        targetUser.setBanned(false);
        userRepository.save(targetUser);
    }
}