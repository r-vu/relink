package rvu.application.relink;

import java.util.Objects;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.lang.NonNull;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@JsonPropertyOrder({"name", "dest", "useCount", "owner"})
public class ShortURL {

    private static final Random RNG = new Random();
    private static final String[] VALID_SCHEMES = { "http", "https" };
    private static final UrlValidator URI_VALIDATOR = new UrlValidator(VALID_SCHEMES);

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("name")
    private String name;

    @NonNull
    @Column(columnDefinition = "TEXT")
    @JsonProperty("destination")
    private String dest;

    @ManyToOne
    @JsonProperty("owner")
    private RelinkUser owner;

    @JsonProperty("useCount")
    private int useCount;

    private ShortURL() {}

    public ShortURL(String name, String dest) {
        this(name, dest, null);
    }

    public ShortURL(String name, String dest, RelinkUser owner) {
        this.name = name;
        this.dest = dest;
        this.owner = owner;
        this.useCount = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setOwner(RelinkUser owner) {
        this.owner = owner;
    }

    public void incrementUseCount() {
        useCount++;
    }

    public void resetName() {
        name = RNG.ints(5, "a".codePointAt(0), "z".codePointAt(0))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public boolean validateDestination() {
        if (!dest.toLowerCase().startsWith("http")) {
            dest = "http://".concat(dest);
        }

        return URI_VALIDATOR.isValid(dest);

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDest() {
        return dest;
    }

    public RelinkUser getOwner() {
        return owner;
    }

    public int getUseCount() {
        return useCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() != ShortURL.class) {
            return false;
        } else {
            ShortURL shortURL = (ShortURL) obj;
            return Objects.equals(id, shortURL.id) &&
            Objects.equals(name, shortURL.name) &&
            Objects.equals(dest, shortURL.dest) &&
            Objects.equals(owner, shortURL.owner) &&
            Objects.equals(useCount, shortURL.useCount);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dest, owner, useCount);
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] Name: %s Destination: %s Owner: %s Use Count: %d",
            id, name, dest, owner == null ? "(none)" : owner.getName(), useCount);
    }

}
