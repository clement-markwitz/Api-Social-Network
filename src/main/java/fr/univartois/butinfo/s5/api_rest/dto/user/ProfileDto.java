// (Utilisé pour la lecture ET la mise à jour du profil)
package fr.univartois.butinfo.s5.api_rest.dto.user;

import java.util.List;

/**
 * DTO (sortie) pour l'objet embarqué Profile.
 */
public record ProfileDto (
    String pseudo,
    String bio,
    String avatarUrl,
    String location,
    List<String> languages
){
}