package rvu.application.urlshortenapp;

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
    }
}