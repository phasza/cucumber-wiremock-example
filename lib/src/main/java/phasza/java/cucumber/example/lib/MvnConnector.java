package phasza.java.cucumber.example.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import phasza.java.cucumber.example.lib.request.MvnHttpUtils;
import phasza.java.cucumber.example.lib.request.QueryProperty;
import phasza.java.cucumber.example.lib.request.RowsProperty;
import phasza.java.cucumber.example.lib.request.WTProperty;
import phasza.java.cucumber.example.lib.response.MvnResponse;
import phasza.java.cucumber.example.lib.response.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for connecting to the maven server and querying through
 * the REST API.
 */
@Singleton
@AllArgsConstructor
public final class MvnConnector {

    /**
     * Injected object mapper for mapping JSON responses
     */
    private final ObjectMapper objectMapper;
    /**
     * Injected logger
     */
    private final Logger logger;

    /**
     * Runs a select query on the maven server and lists packages.
     * An example REST url for query:
     *    http://search.maven.org/solrsearch/select?q=guice&rows=20&wt=json
     *    (documentation https://search.maven.org/classic/#api)
     * @param repositoryURL URL of the maven server to connect (e.g https://search.maven.org)
     * @param pattern Pattern for finding packages. (This is the "q=guice" parameter).
     *                The pattern is matched against an artifact"s ID, which is the [groupID]:[artifactID]
     *                e.g. "com.google.java:guice" matches "com", "google", "gui*" etc.
     * @param rows Limits the number of results to display. (The default on the server side is 10)
     */
    public void runQuery(final String repositoryURL, final String pattern, final int rows) {
        try (var client = HttpClients.createDefault()) {
            //Create a request for the selection with the given query properties
            final var request = new HttpGet(MvnHttpUtils.select(
                    repositoryURL, new QueryProperty(pattern), new RowsProperty(rows), WTProperty.json()));
            //Execute the HTTP request and translate the response into the MvnResponse object
            final var response = client.execute(
                    request,
                    httpResponse -> objectMapper.readValue(httpResponse.getEntity().getContent(), MvnResponse.class));
            //Map the response object to the format to display and sort by ABC
            final var result = Optional.ofNullable(response.getResponse()).map(Response::getDocs)
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(i -> String.format("%s:%s", i.getId(), i.getLatestVersion()))
                    .sorted()
                    .collect(Collectors.toList());

            if (result.isEmpty()) {
                throw new MvnQueryException("No matching artifacts could be found!");
            }
            result.forEach(logger::info);
        } catch (IOException e) {
            throw new MvnQueryException("Repository cannot be reached!", e);
        }
    }
}
