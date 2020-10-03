package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        RelinkUser owner;

        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2User oAuth2User = (OAuth2User) auth.getPrincipal();
            owner = userRepo.findByNameOAuth(oAuth2User.getName(), oAuth2User.getAttributes().keySet().hashCode());
        } else {
            owner = userRepo.findByNameLocal(auth.getName());
        }

        shortURL.setOwner(owner);
    }

}
