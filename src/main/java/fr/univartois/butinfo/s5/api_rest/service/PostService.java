package fr.univartois.butinfo.s5.api_rest.service;

import fr.univartois.butinfo.s5.api_rest.dto.post.PostCreateDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostDto;
import fr.univartois.butinfo.s5.api_rest.dto.post.PostUpdateDto;
import fr.univartois.butinfo.s5.api_rest.mapper.PostMapper;
import fr.univartois.butinfo.s5.api_rest.model.Post;
import fr.univartois.butinfo.s5.api_rest.model.PostStats;
import fr.univartois.butinfo.s5.api_rest.model.User;
import fr.univartois.butinfo.s5.api_rest.repository.PostRepository;
import fr.univartois.butinfo.s5.api_rest.repository.PostStatsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostStatsRepository postStatsRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper , PostStatsRepository postStatsRepository) {
        this.postStatsRepository = postStatsRepository;
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    public PostDto createPost(PostCreateDto dto, String authorId) {
        Post post = postMapper.toEntity(dto);
        User author = new User();
        author.setId(authorId);
        post.setAuthor(author);

        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        PostStats stats = new PostStats(0, 0);
        stats = postStatsRepository.save(stats);
        post.setStats(stats);

        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    public PostDto getPostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post introuvable"));
        return postMapper.toDto(post);
    }

    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toDto)
                .toList();
    }

    public List<PostDto> searchPosts(String keyword) {
        return postRepository.findAllByTextContainingIgnoreCase(keyword).stream()
                .map(postMapper::toDto)
                .toList();
    }

    public PostDto updatePost(String id, PostUpdateDto dto, String requestUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post introuvable"));

        if (!post.getAuthor().getId().equals(requestUserId)) {
            throw new SecurityException("Vous ne pouvez pas modifier ce post");
        }

        postMapper.updatePostFromDto(dto, post);
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return postMapper.toDto(savedPost);
    }

    public void deletePost(String id, String requestUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post introuvable"));

        if (!post.getAuthor().getId().equals(requestUserId)) {
            throw new SecurityException("Vous ne pouvez pas supprimer ce post");
        }

        if (post.getStats() != null && post.getStats().getId() != null) {
            postStatsRepository.deleteById(post.getStats().getId());
        }
        postRepository.delete(post);
    }
}