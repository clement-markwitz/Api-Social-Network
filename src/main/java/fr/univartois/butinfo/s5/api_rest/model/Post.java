package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represent a post created by a user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    @Indexed
    @DBRef
    private User author;

    /**
     * The post is it on a page?
     * Reference to 'Page'.
     */
    @Indexed
    @DBRef
    private Page page;

    /**
     * The post is it in a community?
     * Reference to 'Community'.
     */
    @Indexed
    @DBRef
    private Community community;

    private PostType type;

    private String text;

    private Media media;

    private PostVisibility visibility;

    @DBRef
    private PostStats stats;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}