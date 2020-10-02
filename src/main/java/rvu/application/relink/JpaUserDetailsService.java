package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class JpaUserDetailsService implements UserDetailsService {

    private final RelinkUserRepository userRepo;

    @Autowired
    public JpaUserDetailsService(RelinkUserRepository relinkUserRepo) {
        this.userRepo = relinkUserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        RelinkUser relinkUser = this.userRepo.findByNameLocal(username);
        return new User(relinkUser.getName(), relinkUser.getPassword(), 
        AuthorityUtils.createAuthorityList(relinkUser.getRoles()));
    }

}
