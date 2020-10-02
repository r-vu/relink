package rvu.application.relink;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
public class RelinkUser {

    protected static final PasswordEncoder PASSWORD_HASHER =
    PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String[] roles;

    @JsonIgnore
    private String password;

    @Column(name = "is_oauth", nullable = false)
    private boolean isOAuth;

    @Column(name = "oauth_uuid")
    private Integer oauthUuid;

    protected RelinkUser() {}

    public RelinkUser(String name, String password, String... roles) {
        this.name = name;
        this.setPassword(password);
        this.roles = roles;
        this.isOAuth = false;
    }

    public RelinkUser(OAuth2User oAuth2User) {
        this.name = oAuth2User.getAttribute("id");
        this.roles = new String[] {"ROLE_USER"};
        this.isOAuth = true;
        this.oauthUuid = oAuth2User.getAttributes().hashCode();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String[] getRoles() {
        return roles;
    }

    public boolean isOAuth() {
        return isOAuth;
    }

    public int getOauthUuid() {
        return oauthUuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_HASHER.encode(password);
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return String.format("User (ID: %d Name: %s Roles: %s isOAuth: %s OAuthUUID: %s)",
            id, name, roles, isOAuth, oauthUuid == null ? "" : oauthUuid.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            RelinkUser user = (RelinkUser) obj;
            return Objects.equals(id, user.id) &&
            Objects.equals(name, user.name) &&
            Objects.equals(password, user.password) &&
            Objects.equals(roles, user.roles) &&
            Objects.equals(isOAuth, user.isOAuth) &&
            Objects.equals(oauthUuid, user.oauthUuid);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, roles, isOAuth, oauthUuid);
    }
}
