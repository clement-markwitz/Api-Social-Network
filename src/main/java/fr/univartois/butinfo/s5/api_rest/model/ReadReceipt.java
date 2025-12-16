package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

/**
 * Objet embarqué dans Message.
 * N'est PAS un @Document.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadReceipt {

    /**
     * L'ID de l'utilisateur qui a lu le message.
     */
    @Indexed
    @DBRef
    private User user;

    /**
     * L'heure à laquelle il l'a lu.
     */
    private LocalDateTime readAt;
}