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

@Service
public class BanService {
    private final BanRepository banRepository;
    private final UserRepository userRepository;
    private final BanMapper banMapper;

    public BanService(BanRepository banRepository, UserRepository userRepository, BanMapper banMapper) {
        this.banRepository = banRepository;
        this.userRepository = userRepository;
        this.banMapper = banMapper;
    }

    public List<BanDto> getAllBans() {
        return banRepository.findAll().stream()
                .map(banMapper::toDto)
                .toList();
    }

    public BanDto banUser(String targetUserId, BanCreateDto dto, String moderatorId) {
        // Verify if the user to be banned exists
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));

        // Verify if the user is already banned
        if (banRepository.findByUserIdAndActiveTrue(targetUserId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'utilisateur est déjà banni");
        }

        Ban ban = banMapper.toEntity(dto);

        User moderator = new User();
        moderator.setId(moderatorId);
        ban.setModerator(moderator);

        ban.setActive(true);
        ban.setStartAt(LocalDateTime.now());
        ban.setEndAt(LocalDateTime.now().plusDays(dto.durationDays()));
        ban.setCreatedAt(LocalDateTime.now());
        ban.setUpdatedAt(LocalDateTime.now());

        Ban savedBan = banRepository.save(ban);

        targetUser.setBanned(true);
        userRepository.save(targetUser);

        return banMapper.toDto(savedBan);
    }

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