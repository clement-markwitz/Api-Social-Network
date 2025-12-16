package fr.univartois.butinfo.s5.api_rest.repository;

import fr.univartois.butinfo.s5.api_rest.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    // On recupere tous les commentaires d'un post
    List<Comment> findAllByPostId(String postId);
}
