package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CreateDatabaseEntries implements CommandLineRunner {

    private final ShortURLRepository shortURLRepo;
    private final RelinkUserRepository userRepo;

    @Autowired
    public CreateDatabaseEntries(ShortURLRepository shortURLRepo, RelinkUserRepository userRepo) {
        this.shortURLRepo = shortURLRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... strings) throws Exception {
        // this.shortURLRepo.deleteAll();
        // this.shortURLRepo.save(new ShortURL("Google", "AOL"));
        // this.shortURLRepo.save(new ShortURL("Yahoo", "Netscape"));
        // this.shortURLRepo.save(new ShortURL("Discord", "Skype"));
        // this.shortURLRepo.save(new ShortURL("Microsoft", "Apple"));
        // this.shortURLRepo.save(new ShortURL("nVidia", "AMD"));
        // this.shortURLRepo.save(new ShortURL("Pixar", "Disney"));
        // this.shortURLRepo.save(new ShortURL("test123", "https://www.google.com/"));

        // this.userRepo.deleteAll();
        // this.userRepo.save(new RelinkUser("user1", "password1"));
        // this.userRepo.save(new RelinkUser("user2", "password2"));
        // this.userRepo.save(new RelinkUser("user3", "password3"));
        // this.userRepo.save(new RelinkUser("user4", "password4"));
        // this.userRepo.save(new RelinkUser("user5", "password5"));

        RelinkUser admin = this.userRepo.findByName("admin");
        if (admin == null) {
            this.userRepo.save(new RelinkUser("admin", "password"));
        }

    }
}
