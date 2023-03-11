package phasza.java.cucumber.example.lib.request;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility to create HTTP requests towards the maven server API with the give configuration
 */
public final class MvnHttpUtils {

    /**
     * URI part of the maven server API
     */
    private static final String API_URL = "solrsearch";
    /**
     * URI part of the select API
     */
    private static final String SELECT_URI = "select";

    /**
     * Creates a select query with given URL and properties.
     * e.g. Given repositoryURL=http://test.maven.org
     *      And properties are: QueryProperty("guice"), RowsProperty(20), WTProperty("json")
     *      Then the result is "http://test.maven.org/solrsearch/select?q=guice&rows=20&wt=json"
     * @param repositoryURL URL to server
     * @param properties Properties for the query
     * @return Formatted select URL
     */
    public static String select(
            final String repositoryURL,
            final MvnURLProperty... properties) {
        return String.format(
                "%s/%s/%s?%s",
                repositoryURL,
                API_URL,
                SELECT_URI,
                joinProperties(Arrays.stream(properties)
                        .map(MvnURLProperty::get)
                        .collect(Collectors.toList()))
        );
    }

    private static String joinProperties(final Iterable<String> properties) {
        return String.join("&", properties);
    }

    private MvnHttpUtils() {

    }
}
