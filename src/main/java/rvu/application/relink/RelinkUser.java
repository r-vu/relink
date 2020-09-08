package rvu.application.relink;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class RelinkUser {

    private static final PasswordEncoder PASSWORD_HASHER = 
    PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @JsonIgnore
    private String password;

    private RelinkUser() {}

    public RelinkUser(String name, String password) {
        this.name = name;
        this.setPassword(password);
    }
    
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_HASHER.encode(password);
    }

    @Override
    public String toString() {
        return String.format("User (ID: %d Name: %s)", id, name);
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
            Objects.equals(password, user.password);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password);
    }
}