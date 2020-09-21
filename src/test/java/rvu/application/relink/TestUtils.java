package rvu.application.relink;

import java.util.Random;
import java.lang.StringBuilder;

public class TestUtils {

    private static final Random rng = new Random();

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

    protected static ShortURL randomShortURL() {
        return new ShortURL(randomAlphabeticalString(5), randomURL());
    }

    protected static String randomURL() {
        String protocol = PROTOCOLS[rng.nextInt(PROTOCOLS.length)];
        String subdomain = randomAlphabeticalString(rng.nextInt(5) + 2);
        String host = randomAlphabeticalString(rng.nextInt(5) + 4);
        String tld = TLDS[rng.nextInt(TLDS.length)];

        StringBuilder out = new StringBuilder(protocol);
        out.append(subdomain);
        out.append(".");
        out.append(host);
        out.append(tld);
        out.append("TESTDATA");

        return out.toString();
    }

    protected static String randomAlphabeticalString(int length) {
        return rng.ints(length, "a".codePointAt(0), "z".codePointAt(0)).collect(
            StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
    
}
