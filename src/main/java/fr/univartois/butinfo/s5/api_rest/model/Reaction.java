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
@CompoundIndex(name = "idx_reaction_unique", def = "{'post': 1, 'user': 1, 'type': 1}", unique = true)
public class Reaction {

    @Id
    private String id;

    @Indexed
    @DBRef
    private Post post;

    @Indexed
    @DBRef
    private User user;

    @Indexed
    @DBRef
    private Comment comment;

    /**
     * The type of reaction (LIKE, HELPFUL, etc.).
     */
    private ReactionType type;

    @CreatedDate
    private LocalDateTime createdAt;
}