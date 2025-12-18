package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent a comment on a post.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @Indexed
    @DBRef
    private Post post;

    @Indexed
    @DBRef
    private User author;

    private String text;

    @Indexed
    private Comment parentComment;

    /**
     * Liste des ID des utilisateurs qui ont liké ce commentaire.
     * Utilisation d'un Set pour garantir l'unicité (pas de doublons).
     */
    private Set<String> likedBy = new HashSet<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Méthode utilitaire pour obtenir le nombre de likes (utilisé par le front/DTO).
     */
    public int getLikeCount() {
        return (likedBy == null) ? 0 : likedBy.size();
    }
}