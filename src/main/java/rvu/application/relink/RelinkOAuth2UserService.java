package rvu.application.relink;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * <p>
 * The purpose of this service is to prepend a unique OAuth2 identifier to the
 * default OAuth2User's name. Doing this solves two security cases of potential
 * exploit:
 * <ol><li>
 * A local user account with a username equal to the user ID given to the
 * application by the OAuth2 provider.
 * </li><li>
 * An OAuth2 user account with a user ID equal to a user ID given by a
 * different provider.
 * </li></ol>
 * <p>
 * In both cases, one account would be able to see the owned shortened links of
 * the other account with the current query definition in ShortURLRepository.
 */
public class RelinkOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        Map<String, Object> attributes = user.getAttributes();
        Map<String, Object> newAttributes = new LinkedHashMap<>(attributes);
        Integer oAuthProvider = attributes.keySet().hashCode();
        String nameKey = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint().getUserNameAttributeName();

        newAttributes.replace(nameKey, oAuthProvider.toString() + ":" + user.getName());
        return new DefaultOAuth2User(user.getAuthorities(), newAttributes, nameKey);
    }

}
