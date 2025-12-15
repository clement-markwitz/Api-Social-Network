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
@Document(collection = "pageSubscriptions")
@CompoundIndex(name = "user_page_unique_idx", def = "{'userId': 1, 'pageId': 1}", unique = true)
public class PageSubscription {

    @Id
    private String id;

    @Indexed
    private User user; // L'utilisateur qui s'abonne

    @Indexed
    private Page page; // La page à laquelle il s'abonne

    @CreatedDate
    private LocalDateTime createdAt;
}