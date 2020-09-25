package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
@RepositoryEventHandler(ShortURL.class)
public class ShortURLEventHandler {

    private final RelinkUserRepository userRepo;

    @Autowired
    public ShortURLEventHandler(RelinkUserRepository userRepo) {
        this.userRepo = userRepo;
    }


    @HandleBeforeCreate
    @HandleBeforeSave
    public void addOwnerInfoToShortURL(ShortURL shortURL) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        RelinkUser user = this.userRepo.findByName(name);
        if (user != null) {
            shortURL.setOwner(user);
        }
    }

}
