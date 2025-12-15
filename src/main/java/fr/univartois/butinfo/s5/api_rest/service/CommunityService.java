package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Community;
import fr.univartois.butinfo.s5.api_rest.repository.CommunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;

    public CommunityService(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    public Community findById(String id) {
        return communityRepository.findById(id).orElse(null);
    }

    public Community createCommunity(Community community) {
        return communityRepository.save(community);
    }

    public Community updateCommunity(Community community) {
        return communityRepository.save(community);
    }

    public boolean deleteCommunity(String id) {
        if (!communityRepository.existsById(id)) {
            return false;
        }
        communityRepository.deleteById(id);
        return true;
    }

}
