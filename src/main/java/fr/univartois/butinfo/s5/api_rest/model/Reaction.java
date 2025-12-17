package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represent a reaction (like, helpful, etc.) given by a user to a post.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reactions")
// Index unique : empêche un utilisateur de donner 2x la MÊME réaction au MÊME post.
// Il peut "liker" ET "trouver utile" si les types sont différents.
@CompoundIndex(name = "idx_reaction_unique", def = "{'post': 1, 'user': 1, 'type': 1}", unique = true)
public class Reaction {

    @Id
    private String id;

    @Indexed // Index pour trouver toutes les réactions d'un post
    @DBRef
    private Post post;

    @Indexed // Index pour trouver toutes les réactions d'un user
    @DBRef
    private User user;

    /**
     * The type of reaction (LIKE, HELPFUL, etc.).
     */
    private ReactionType type;

    @CreatedDate
    private LocalDateTime createdAt;
}