package rvu.application.relink;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BasicGetRequestTest {

    @Autowired
    private MockMvc mockMvc;

    // Uncomment .andDo(print()) if you want output to be
    // seen in the console
    // from MockMvcResultHandlers

    @Test
    public void getHome() throws Exception {
        this.mockMvc.perform(get("/"))//.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("home.css")));
    }

    @Test
    public void getLogin() throws Exception {
        this.mockMvc.perform(get("/login"))//.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("login.css")));
    }

    @Test
    public void getSignup() throws Exception {
        this.mockMvc.perform(get("/signup"))//.andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("signup.css")));
    }

    // Should redirect to login without auth
    @Test
    public void getTable() throws Exception {
        this.mockMvc.perform(get("/table"))//.andDo(print())
            .andExpect(status().is3xxRedirection());
    }

    // Should redirect to home directly
    @Test
    public void getLogout() throws Exception {
        this.mockMvc.perform(get("/logout"))//.andDo(print())
            .andExpect(status().is3xxRedirection());
    }


}