package rvu.application.relink;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rvu.application.relink.TestUtils.randomAlphabeticalString;
import static rvu.application.relink.TestUtils.randomURL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED, connection = EmbeddedDatabaseConnection.H2)
@AutoConfigureMockMvc
public class BasicPostRequestTest {

    @Autowired
    private MockMvc mockMvc;

    // .andDo(MockMvcResultHandlers.print()) if you want output to be
    // seen in the console

    @Test
    public void postHomeNoData() throws Exception {
        ShortURLFormData input = new ShortURLFormData();
        input.setDest(randomURL());

        this.mockMvc.perform(
            post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .flashAttr("shortURLFormData", input)
            .with(csrf()))//.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("success.css")));
    }

    @Test
    public void postSignupNoData() throws Exception {
        RelinkUser input = new RelinkUser();
        input.setName(randomAlphabeticalString(5, 32));
        input.setPassword(randomAlphabeticalString(5, 70));

        this.mockMvc.perform(
            post("/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .flashAttr("newUser", input)
            .with(csrf()))//.andDo(MockMvcResultHandlers.print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    // Should redirect to home directly, same as GET
    @Test
    public void postLogout() throws Exception {
        this.mockMvc.perform(
            post("/logout")
            .with(csrf()))//.andDo(MockMvcResultHandlers.print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }


}
