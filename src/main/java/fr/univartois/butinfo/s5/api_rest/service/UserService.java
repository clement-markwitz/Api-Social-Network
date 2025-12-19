package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.BlockRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for user-related operations.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BlockRepository blockRepository;

    /**
     * Constructor for UserService.
     * @param userRepository userRepository
     */
    public UserService(UserRepository userRepository, BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get user by id.
     *
     * @param id user id
     * @return User
     */
    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable"));
    }

    /**
     * Check if current user has rights to modify target user.
     *
     * @param targetUserId target user id
     * @param currentUser current user
     */
    public void checkUserRights(String targetUserId, User currentUser) {
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        boolean isSelf = currentUser.getId().equals(targetUserId);

        if (!isSelf && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'avez pas le droit de modifier ce profil.");
        }
    }

    /**
     * Get all users.
     *
     * @return List of users
     */
    public List<User> getAll() {
        return userRepository.findAll();
    }


    /**
     * Check if user exists by id.
     *
     * @param id user id
     * @return boolean
     */
    public boolean delete(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Load user by username for authentication.
     *
     * @param username username
     * @return UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    }

    /**
     * Search users by pseudo, excluding those who blocked the current user.
     *
     * @param query search query
     * @param currentUserId ID of the user performing the search
     * @return List of users matching the query and accessible
     */
    public List<User> searchUsers(String query, String currentUserId) {
        List<User> foundUsers;
        if (query == null || query.isBlank()) {
            foundUsers = getAll();
        } else {
            foundUsers = userRepository.findByProfilePseudoContainingIgnoreCase(query);
        }
        return foundUsers.stream()
                .filter(u -> !blockRepository.existsByBlockerIdAndBlockedId(u.getId(), currentUserId))
                .toList();
    }
}