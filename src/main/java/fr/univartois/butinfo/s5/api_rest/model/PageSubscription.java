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
 * Represent a subscription of a user to a page.
 * Users can subscribe to pages to receive updates and content from them.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pageSubscriptions")
@CompoundIndex(name = "user_page_unique_idx", def = "{'userId': 1, 'pageId': 1}", unique = true)
public class PageSubscription {

    @Id
    private String id;

    @Indexed
    @DBRef
    private User user; // L'utilisateur qui s'abonne

    @Indexed
    @DBRef
    private Page page; // La page à laquelle il s'abonne

    @CreatedDate
    private LocalDateTime createdAt;
}