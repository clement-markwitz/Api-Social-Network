package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

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
    @Indexed // Indexe le champ userId dans l'objet embarqué
    private String userId;

    /**
     * L'heure à laquelle il l'a lu.
     */
    private LocalDateTime readAt;
}