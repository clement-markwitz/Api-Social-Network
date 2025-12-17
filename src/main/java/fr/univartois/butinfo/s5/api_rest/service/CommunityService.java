package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.CommunityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Service class for managing communities.
 */
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    /**
     * Verify if the user has admin rights in the community.
     * @param community
     * @param user
     */
    public void checkAdminRights(Community community, User user) {
        boolean isAdmin = community.getAdmins() != null && community.getAdmins().stream()
                .anyMatch(admin -> admin.getId().equals(user.getId()));

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous devez être administrateur de cette communauté pour effectuer cette action.");
        }
    }

    /**
     * Get all communities.
     * @return
     */
    public List<Community> getAll() {
        return communityRepository.findAll();
    }

    /**
     * Get a community by its ID.
     * @param id
     * @return
     */
    public Community getById(String id) {
        return communityRepository.findById(id).orElse(null);
    }

    /**
     * Create a new community.
     * @param community
     * @return
     */
    public Community createCommunity(Community community) {
        return communityRepository.save(community);
    }

    /**
     * Update an existing community.
     * @param community
     * @return
     */
    public Community updateCommunity(Community community) {
        return communityRepository.save(community);
    }

    /**
     * Delete a community by its ID.
     * @param id
     * @return
     */
    public boolean deleteCommunity(String id) {
        if (!communityRepository.existsById(id)) {
            return false;
        }
        communityRepository.deleteById(id);
        return true;
    }

}
