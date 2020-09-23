package rvu.application.relink;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rvu.application.relink.TestUtils.randomShortURL;
import static rvu.application.relink.TestUtils.toJson;

import javax.servlet.Filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
// import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED, connection = EmbeddedDatabaseConnection.H2)
@WebAppConfiguration
@AutoConfigureMockMvc
public class ApiPostRequestTest {

    // .andDo(MockMvcResultHandlers.print())
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(springSecurityFilterChain).build();
    }

    private RequestPostProcessor userInfo() {
        return user("user")
            .password("password")
            .roles("USER");
    }

    @Test
    public void noCSRFRequest() throws Exception {
        mockMvc.perform(
            post("/api/shortURLs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(randomShortURL()))
            .with(userInfo()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void withCSRFRequest() throws Exception {
        mockMvc.perform(
            post("/api/shortURLs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(randomShortURL()))
            .with(userInfo())
            .with(csrf()))
            .andExpect(status().isCreated());
    }

    @Test
    public void noAuthRequest() throws Exception {
        mockMvc.perform(
            post("/api/shortURLs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(randomShortURL())))
            .andExpect(status().isForbidden());
    }

}
