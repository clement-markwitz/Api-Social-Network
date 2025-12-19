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
    public boolean isCommunityAdmin(Community community, User user) {
        return community.getAdmins() != null && community.getAdmins().stream()
                .anyMatch(admin -> admin.getId().equals(user.getId()));
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
        return communityRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
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
    public void deleteCommunity(String id) {
        if (!communityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found");
        }
        communityRepository.deleteById(id);
    }

}
