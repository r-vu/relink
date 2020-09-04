package rvu.application.relink;

import java.util.Random;

public class ShortURLFormData {
    
    private String dest;

    public ShortURLFormData() {
        
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public ShortURL toShortURL() {
        Random rng = new Random();
        String name = rng.ints(5, "a".codePointAt(0), "z".codePointAt(0)).collect(
            StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
        return new ShortURL(name, dest);
    }
}
