package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represent statistics related to a post, such as number of reactions and comments.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post_stats")
public class PostStats {

    @Id
    private String id;

    private int reactions;
    private int comments;

    public PostStats(int reactions, int comments) {
        this.reactions = reactions;
        this.comments = comments;
    }
}