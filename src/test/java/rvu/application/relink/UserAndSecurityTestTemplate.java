package rvu.application.relink;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import javax.servlet.Filter;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
public class UserAndSecurityTestTemplate {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    protected MockMvc mockMvc;

    protected static final String SHORTURL_API = "/api/shortURLs";

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(springSecurityFilterChain).build();
    }

    protected RequestPostProcessor userAInfo() {
        return user("userA")
            .password("password")
            .roles("USER");
    }

    protected RequestPostProcessor userBInfo() {
        return user("userB")
            .password("password")
            .roles("USER");
    }

    protected RequestPostProcessor adminInfo() {
        return user("admin")
            .password("password")
            .roles("USER", "ADMIN");
    }

}
