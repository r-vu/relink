package rvu.application.relink;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED, connection = EmbeddedDatabaseConnection.H2)
class RelinkApplicationTests {

    @Autowired
    private HomeController controller;

    @Test
    void contextLoads() {
        
    }

    public void controllerCheck() throws Exception {
        assertThat(controller).isNotNull();
    }

}
