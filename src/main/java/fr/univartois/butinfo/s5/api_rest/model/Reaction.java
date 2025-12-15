package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reactions")
// Index unique : empêche un utilisateur de donner 2x la MÊME réaction au MÊME post.
// Il peut "liker" ET "trouver utile" si les types sont différents.
@CompoundIndex(name = "post_user_type_unique_idx", def = "{'postId': 1, 'userId': 1, 'type': 1}", unique = true)
public class Reaction {

    @Id
    private String id;

    @Indexed // Index pour trouver toutes les réactions d'un post
    private Post post;

    @Indexed // Index pour trouver toutes les réactions d'un user
    private User user;

    /**
     * Le type de réaction (ex: LIKE, HELPFUL, LAUGH).
     * Géré via un Enum pour la robustesse.
     */
    private ReactionType type;

    @CreatedDate
    private LocalDateTime createdAt;
}