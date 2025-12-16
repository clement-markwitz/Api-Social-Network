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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @Indexed // Index VITAL pour lister tous les commentaires d'un post
    @DBRef
    private Post post;

    @Indexed // Index pour lister tous les commentaires d'un auteur
    @DBRef
    private User author;

    private String text;

    /**
     * Pour les réponses imbriquées (répondre à un autre commentaire).
     * Si null, c'est un commentaire de premier niveau.
     */
    @Indexed
    private Comment parentComment;

    /**
     * Nombre de "likes" sur CE commentaire (dénormalisé).
     */
    private int likeCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt; // Utile si les commentaires sont éditables
}