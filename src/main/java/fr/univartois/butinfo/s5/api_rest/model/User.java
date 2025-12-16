package fr.univartois.butinfo.s5.api_rest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User implements UserDetails {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String role;

    // Objets embarqués
    // On dit à SonarQube d'ignorer l'erreur de sérialisation ici pour ne pas casser la DB MongoDB
    @SuppressWarnings("java:S1948")
    private Profile profile;

    @SuppressWarnings("java:S1948")
    private Preferences prefs;

    @SuppressWarnings("java:S1948")
    private Interests interests;

    private boolean banned;

    @CreatedDate // Géré automatiquement si l'audit est activé
    private LocalDateTime createdAt;

    @LastModifiedDate // Géré automatiquement si l'audit est activé
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + (role == null ? "USER" : role)));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !banned;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // NOTE : J'ai supprimé tous les Getters, Setters et toString() manuels.
    // L'annotation @Data de Lombok (ligne 19) s'en occupe déjà !
}