package rvu.application.relink;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static rvu.application.relink.TestUtils.randomShortURL;
import static rvu.application.relink.TestUtils.randomURL;
import static rvu.application.relink.TestUtils.toJson;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
public class ApiPostRequestTest extends UserAndSecurityTestTemplate {

    // .andDo(MockMvcResultHandlers.print())

    @Test
    public void noCSRFRequest() throws Exception {
        mockMvc.perform(
            post(SHORTURL_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(randomShortURL()))
            .with(userAInfo()))
            .andExpect(status().isForbidden());
    }

    @Test
    public void withCSRFRequest() throws Exception {
        mockMvc.perform(
            post(SHORTURL_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(randomShortURL()))
            .with(userAInfo())
            .with(csrf()))
            .andExpect(status().isCreated());
    }

    @Test
    public void noAuthRequest() throws Exception {
        mockMvc.perform(
            post(SHORTURL_API)
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(randomShortURL())))
            .andExpect(status().isForbidden());
    }

}
