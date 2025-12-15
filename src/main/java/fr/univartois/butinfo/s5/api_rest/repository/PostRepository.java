package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findAllByAuthorId(String authorId, Pageable pageable);

    Page<Post> findAllByCommunityId(String communityId, Pageable pageable);

    Page<Post> findAllByPageId(String pageId, Pageable pageable);


}
