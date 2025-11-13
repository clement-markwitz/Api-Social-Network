package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    @Indexed
    private String authorId; // Référence à 'User'

    /**
     * Le post est-il sur une page ?
     * Référence à 'Page'.
     */
    @Indexed
    private String pageId; // Null si ce n'est pas un post de page

    /**
     * Le post est-il dans une communauté ?
     * Référence à 'Community'.
     */
    @Indexed
    private String communityId; // Null si ce n'est pas un post de communauté

    // Si pageId et communityId sont null, c'est un post de statut personnel.

    private PostType type; // ex: STATUS, RECIPE, ...

    private String text;

    private Media media; // Objet embarqué

    private PostVisibility visibility; // ex: PUBLIC, MEMBERS_ONLY

    private PostStats stats; // Objet embarqué

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}