package rvu.application.relink;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HTTPServiceTest {
    
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String root = "http://localhost:";

    // Unit Tests to check if each page is correctly loading

    @Test
    public void HomepageValidateTest() throws Exception {
        assertThat(
            testRestTemplate.getForObject(root + port + "/", String.class))
                .contains("home.css");
    }

    @Test
    public void LoginValidateTest() throws Exception {
        assertThat(
            testRestTemplate.getForObject(root + port + "/login", String.class))
                .contains("login.css");
    }

    @Test
    public void SignupValidateTest() throws Exception {
        assertThat(
            testRestTemplate.getForObject(root + port + "/signup", String.class))
                .contains("signup.css");
    }

    @Test
    public void TableNoLoginValidateTest() throws Exception {
        // Should redirect to /login since /table requires authenication
        assertThat(
            testRestTemplate.getForObject(root + port + "/table", String.class))
                .contains("login.css");
    }
}