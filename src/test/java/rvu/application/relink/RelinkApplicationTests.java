package rvu.application.relink;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
