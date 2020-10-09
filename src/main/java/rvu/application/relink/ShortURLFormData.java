package rvu.application.relink;

public class ShortURLFormData {

    private String dest;

    public ShortURLFormData() {}

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public ShortURL toShortURL() {
        ShortURL output = new ShortURL("", dest);
        output.resetName();
        return output;
    }
}
