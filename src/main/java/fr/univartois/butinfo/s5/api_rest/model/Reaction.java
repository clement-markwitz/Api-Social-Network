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
// Index unique : Un utilisateur ne peut avoir qu'une seule réaction par post.
@CompoundIndex(name = "unique_user_post_reaction", def = "{'post': 1, 'user': 1}", unique = true)
public class Reaction {

    @Id
    private String id;

    @Indexed
    @DBRef
    private Post post;

    @Indexed
    @DBRef
    private User user;

    private ReactionType type;

    @CreatedDate
    private LocalDateTime createdAt;
}