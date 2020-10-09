package rvu.application.relink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JpaUserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService)
            .passwordEncoder(RelinkUser.PASSWORD_HASHER);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors()
            .and().authorizeRequests()
                .antMatchers(
                    "/signup",
                    "/to/**",
                    "/built/**",
                    "/*.css",
                    "/api/**"
                ).permitAll()
                .anyRequest().authenticated()
            .and().formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            .and().oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(this.oAuth2UserService()))
            .and().logout()
                .logoutSuccessUrl("/")
                .permitAll();
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return new RelinkOAuth2UserService();
    }

}
