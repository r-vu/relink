package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CreateDatabaseEntries implements CommandLineRunner {

    private final ShortURLRepository repo;

    @Autowired
    public CreateDatabaseEntries(ShortURLRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... strings) throws Exception {
        this.repo.deleteAll();
        this.repo.save(new ShortURL("Google", "AOL"));
        this.repo.save(new ShortURL("Yahoo", "Netscape"));
        this.repo.save(new ShortURL("Discord", "Skype"));
        this.repo.save(new ShortURL("Microsoft", "Apple"));
        this.repo.save(new ShortURL("nVidia", "AMD"));
        this.repo.save(new ShortURL("Pixar", "Disney"));
        this.repo.save(new ShortURL("test123", "https://www.google.com/"));
    }
}