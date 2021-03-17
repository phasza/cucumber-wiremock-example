package phasza.java.cucumber.example.lib.request;

import lombok.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Implements the 'q' or 'query' property for the MVN selection
 */
@Value
public class QueryProperty implements MvnURLProperty {

    /**
     * Name of the property in the URL
     */
    public static final String NAME = "q";
    /**
     * Pattern to query
     */
    String pattern;

    @Override
    public String get() {
        return String.format("%s=%s", NAME, URLEncoder.encode(getPattern(), StandardCharsets.UTF_8));
    }
}
