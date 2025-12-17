package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.repository.CommunityRepository;
import fr.univartois.butinfo.s5.api_rest.repository.MessageRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final MessageRepository messageRepository;

    public StatsService(UserRepository userRepository, PostRepository postRepository, CommunityRepository communityRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.messageRepository = messageRepository;
    }

    public long countUsers() {
        return userRepository.count();
    }

    public long countPosts() {
        return postRepository.count();
    }

    public long countCommunities() {
        return communityRepository.count();
    }

    public long countMessages() {
        return messageRepository.count();
    }
}