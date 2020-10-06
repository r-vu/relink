package rvu.application.relink;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rvu.application.relink.TestUtils.randomShortURL;
import static rvu.application.relink.TestUtils.toJson;

import javax.servlet.Filter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * <p>
 * The template class for any tests requiring interaction with the API. Has
 * three mock users to choose from and autowired repositories to better simulate
 * the real application.
 *
 * <p>
 * By default the template is set to use an in-memory H2 database which will be
 * deleted upon application termination. If the created test data needs to be
 * saved for debugging, Comment the {@code @AutoConfigureTestDatabase}
 * annotation, and the repository initialization methods {@code setupOnce()},
 * {@code breakdownEach()}, {@code breakdownAll()} in order to use the database
 * connection you have set in application.properties or elsewhere. The default
 * account with username {@code relinkadmin} and password {@code relinkpassword}
 * should be created if it does not exist.
 *
 * <p>
 * Important Notes:
 *
 * <ol>
 * <li>Despite an existing {@code TestInstance} annotation set to
 * {@code Lifecycle.PER_CLASS}, it seems classes share the same instance of the
 * repositories, so the user repository is cleaned with {@code breakdownAll()}.
 *
 */
@WebAppConfiguration
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED, connection = EmbeddedDatabaseConnection.H2)
public class UserAndSecurityTestTemplate {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private JpaUserDetailsService userDetailsService;

    protected static final String SHORTURL_API = "/api/shortURLs";

    @Autowired
    protected RelinkUserRepository userRepo;

    @Autowired
    protected ShortURLRepository shortURLRepo;

    protected MockMvc mockMvc;

    @BeforeAll
    public void setupOnce() {
        userRepo.save(new RelinkUser("testUserA", "passwordA", "ROLE_USER"));
        userRepo.save(new RelinkUser("testUserB", "passwordB", "ROLE_USER"));
        userRepo.save(new RelinkUser("testAdmin", "passwordAdmin", "ROLE_USER", "ROLE_ADMIN"));
    }

    @BeforeEach
    public void setupEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain)
                .build();
    }

    @AfterEach
    public void breakdownEach() {
        shortURLRepo.deleteAll();
    }

    @AfterAll
    public void breakdownAll() {
        userRepo.deleteAll();
    }

    protected RequestPostProcessor userAInfo() {
        return user(userDetailsService.loadUserByUsername("testUserA"));
    }

    protected RequestPostProcessor userBInfo() {
        return user(userDetailsService.loadUserByUsername("testUserB"));
    }

    protected RequestPostProcessor adminInfo() {
        return user(userDetailsService.loadUserByUsername("testAdmin"));
    }

    protected void postApiRandomShorturl(RequestPostProcessor userInfo) throws Exception {
        mockMvc.perform(
            post(SHORTURL_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(randomShortURL()))
            .with(userInfo)
            .with(csrf()))
            .andExpect(status().isCreated());
    }

    protected void getApiShorturlsAsUserExpectAmount(RequestPostProcessor userInfo, int expectedAmount) throws Exception {
        mockMvc.perform(
            get(SHORTURL_API)
            .with(userInfo)
            .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(
                String.format("\"totalElements\" : %d", expectedAmount)
            )));
    }

}
