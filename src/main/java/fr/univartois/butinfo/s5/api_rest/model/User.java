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

/**
 * Represents a user in the social media application.
 */
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

    @SuppressWarnings("java:S1948")
    private Profile profile;

    @SuppressWarnings("java:S1948")
    private Preferences prefs;

    @SuppressWarnings("java:S1948")
    private Interests interests;

    private boolean banned;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Returns the authorities granted to the user.
     *
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + (role == null ? "USER" : role)));
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    /**
     * Indicates whether the user's account is locked or unlocked.
     *
     * @return true if the account is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return !banned;
    }
    /**
     * Indicates whether the user's credentials (password) are expired or valid.
     *
     * @return true if the credentials are valid, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}