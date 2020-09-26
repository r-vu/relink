package rvu.application.relink;

import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    private static final Random rng = new Random();
    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String[] PROTOCOLS = {
        "http://",
        "https://"
    };

    private static final String[] TLDS = {
        ".com",
        ".uk",
        ".cn",
        ".de",
        ".tk",
        ".net",
        ".org",
        ".gov",
        ".ca"
    };

    public static ShortURL randomShortURL() {
        return new ShortURL(randomAlphabeticalString(5), randomURL());
    }

    public static String randomURL() {
        String protocol = PROTOCOLS[rng.nextInt(PROTOCOLS.length)];
        String subdomain = randomAlphabeticalString(3, 10);
        String host = randomAlphabeticalString(3, 10);
        String tld = TLDS[rng.nextInt(TLDS.length)];

        StringBuilder out = new StringBuilder(protocol);
        out.append(subdomain);
        out.append(".");
        out.append(host);
        out.append(tld);
        out.append("/TESTDATA");

        return out.toString();
    }

    public static String randomAlphabeticalString(int minLength, int maxLength) {
        return randomAlphabeticalString(randomInt(minLength, maxLength));
    }

    public static String randomAlphabeticalString(int length) {
        return rng.ints(length, "a".codePointAt(0), "z".codePointAt(0)).collect(
            StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    public static String toJson(Object obj) throws JsonProcessingException {
        return objMapper.writeValueAsString(obj);
    }

    /**
     * Inclusive of both lower and upper bounds
     */
    public static int randomInt(int lowerBound, int upperBound) {
        upperBound++;
        return (int) (rng.nextDouble() * (upperBound - lowerBound) + lowerBound);
    }

}
