package fr.univartois.butinfo.s5.api_rest.dto;

/**
 * DTO (sortie) minimal pour afficher un utilisateur dans une liste
 * (ex: auteur d'un post, membre d'un groupe).
 */
public record UserSummaryDto(
        String id,
        String pseudo,
        String avatarUrl
) {
}