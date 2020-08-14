package rvu.application.urlshortenapp;

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

    @Override
    public String toString() {
        return String.format("[ID: %d] Name: %s Destination: %s", id, name, dest);
    }
}