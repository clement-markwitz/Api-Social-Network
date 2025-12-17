package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.PostStats;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostStatsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;

    public PostService(PostRepository postRepository, PostStatsRepository postStatsRepository) {
        this.postRepository = postRepository;
        this.postStatsRepository = postStatsRepository;
    }

    public Post createPost(Post post,  User author) {

        post.setAuthor(author);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        PostStats stats = new PostStats(0, 0);
        stats = postStatsRepository.save(stats);
        post.setStats(stats);

        return postRepository.save(post);
    }

    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post introuvable"));
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> searchPosts(String keyword) {
        return postRepository.findAllByTextContainingIgnoreCase(keyword);
    }

    public Post updatePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePost(Post post) {
        if (post.getStats() != null && post.getStats().getId() != null) {
            postStatsRepository.deleteById(post.getStats().getId());
        }
        postRepository.delete(post);
    }
}