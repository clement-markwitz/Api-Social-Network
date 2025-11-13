// (Utilisé pour la lecture ET la mise à jour du profil)
package fr.univartois.butinfo.s5.api_rest.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProfileDto {
    private String pseudo;
    private String bio;
    private String avatarUrl;
    private String location;
    private List<String> languages;
}