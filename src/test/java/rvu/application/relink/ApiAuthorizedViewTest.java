package rvu.application.relink;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static rvu.application.relink.TestUtils.randomInt;
import static rvu.application.relink.TestUtils.randomURL;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

/**
 *
 * <p>
 * Integration test for ownership restricted views of ShortURLs
 * 
 * <p>
 * Regular users should only be able to see what they own, and nothing else.
 * Admin users should be able to see everything, even ShortURLs with no declared
 * owner.
 *
 */
@SpringBootTest
public class ApiAuthorizedViewTest extends UserAndSecurityTestTemplate {

    @Test
    public void createNothingViewWithAll() throws Exception {
        getApiShorturlsAsUserExpectAmount(userAInfo(), 0);
        getApiShorturlsAsUserExpectAmount(userBInfo(), 0);
        getApiShorturlsAsUserExpectAmount(adminInfo(), 0);
    }

    @Test
    public void createOwnViewOwn() throws Exception {
        int userACount = randomInt(5, 20);
        for (int idx = 0; idx < userACount; idx++) {
            postApiRandomShorturl(userAInfo());
        }

        getApiShorturlsAsUserExpectAmount(userAInfo(), userACount);
    }

    @Test
    public void createWithBViewWithA() throws Exception {
        int userBCount = randomInt(5, 20);
        for (int idx = 0; idx < userBCount; idx++) {
            postApiRandomShorturl(userBInfo());
        }

        getApiShorturlsAsUserExpectAmount(userAInfo(), 0);
    }

    @Test
    public void createWithBothViewWithAll() throws Exception {
        int userACount = randomInt(5, 20);
        int userBCount = randomInt(5, 20);

        for (int idx = 0; idx < userACount; idx++) {
            postApiRandomShorturl(userAInfo());
        }

        for (int idx = 0; idx < userBCount; idx++) {
            postApiRandomShorturl(userBInfo());
        }

        getApiShorturlsAsUserExpectAmount(userAInfo(), userACount);
        getApiShorturlsAsUserExpectAmount(userBInfo(), userBCount);
        getApiShorturlsAsUserExpectAmount(adminInfo(), userACount + userBCount);
    }

    @Test
    public void createWithNoOwnerViewWithAll() throws Exception {
        int nullCount = randomInt(5, 20);
        
        for (int idx = 0; idx < nullCount; idx++) {
            postRandomShorturlNoOwner();
        }

        getApiShorturlsAsUserExpectAmount(userAInfo(), 0);
        getApiShorturlsAsUserExpectAmount(userBInfo(), 0);
        getApiShorturlsAsUserExpectAmount(adminInfo(), nullCount);
    }

    @Test
    public void createWithOptionalOwnerViewWithAll() throws Exception {
        int userACount = randomInt(5, 20);
        int userBCount = randomInt(5, 20);
        int nullCount = randomInt(5, 20);

        for (int idx = 0; idx < userACount; idx++) {
            postApiRandomShorturl(userAInfo());
        }

        for (int idx = 0; idx < userBCount; idx++) {
            postApiRandomShorturl(userBInfo());
        }

        for (int idx = 0; idx < nullCount; idx++) {
            postRandomShorturlNoOwner();
        }

        getApiShorturlsAsUserExpectAmount(userAInfo(), userACount);
        getApiShorturlsAsUserExpectAmount(userBInfo(), userBCount);
        getApiShorturlsAsUserExpectAmount(adminInfo(), userACount + userBCount + nullCount);
    }

    private void postRandomShorturlNoOwner() throws Exception {
        ShortURLFormData input = new ShortURLFormData();
        input.setDest(randomURL());

        this.mockMvc.perform(
            post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .flashAttr("shortURLFormData", input)
            .with(csrf()));
    }

}
