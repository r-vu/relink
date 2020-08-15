package rvu.application.urlshortenapp;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ShortURL {

    private @Id @GeneratedValue Long id;
    private String name;
    private String dest; // destination

    private ShortURL() {}

    public ShortURL(String name, String dest) {
        this.name = name;
        this.dest = dest;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDest(String dest) {
        this.dest = dest;
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
            Objects.equals(dest, shortURL.dest);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dest);
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] Name: %s Destination: %s", id, name, dest);
    }
}