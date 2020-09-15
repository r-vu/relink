package rvu.application.relink;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
// @JsonSerialize(using = ShortURLSerializer.class)
@JsonPropertyOrder({"name", "dest", "useCount", "owner"})
public class ShortURL {

    @Id
    @GeneratedValue
    private Long id;

    @JsonProperty("name")
    private String name;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("destination")
    private String dest; // destination

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
        this.useCount++;
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
