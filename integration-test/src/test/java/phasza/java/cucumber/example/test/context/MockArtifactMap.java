package phasza.java.cucumber.example.test.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import phasza.java.cucumber.example.lib.request.RowsProperty;
import phasza.java.cucumber.example.lib.response.MvnResponse;
import phasza.java.cucumber.example.lib.response.Response;
import phasza.java.cucumber.example.test.data.MockArtifact;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * We need to be able to intercept requests which are stubbed for the wiremock in order
 * to be able to shape the proper response for a query according to what we have set up
 * in the scenarios.
 * Wiremock stubbings are not dynamic, meaning that we cant attach a consumer or supplier
 * method to a stub. But we can attach transformer services, which can shape a request dynamically.
 * A stub with transform looks like the following:
 * <code>
 * server.stubFor(get(urlMatching("/solrsearch/select\\?.*"))
 *                  .willReturn(aResponse()
 *                  .withStatus(200)
 *                  .withTransformers(MockArtifactMap.class.getName())));
 * </code>
 * The code above will return a 200 (OK) response to any URL matching the /solrsearch/select? pattern
 * but the response is transformed by the transformer which has the given name.
 */
@Singleton
@AllArgsConstructor
public final class MockArtifactMap extends ResponseDefinitionTransformer {

    /**
     * Injected JSON mapper
     */
    private final ObjectMapper mapper;
    /**
     * Registered mocked artifacts from steps
     */
    private final Map<String, MockArtifact> data = new ConcurrentHashMap<>();

    /**
     * Puts a new mocked artifact to the list of artifact mocked
     * @param id Id of the artifact
     * @param mockArtifact Artifact instance
     */
    public void put(final String id, final MockArtifact mockArtifact) {
        data.put(id, mockArtifact);
    }

    /**
     * Transforms the response for the intercepted HTTP request according to the
     * registered artifacts in the artifact map.
     * @param request Intercepted request
     * @param responseDefinition Current response definition
     * @param files Files for the response (not used)
     * @param parameters Parameters for the response (not used)
     * @return Definition of the new response
     */
    @Override
    public ResponseDefinition transform(
            final Request request,
            final ResponseDefinition responseDefinition,
            final FileSource files,
            final Parameters parameters) {
        //Split of the query character (?)
        //If the split does not contain 2 parts, then the request is ill-formatted
        //Probably the transformer was used on a stubbing where it should not be used
        final var queryParts = request.getAbsoluteUrl().split("\\?");
        if (queryParts.length != 2) {
            throw new RuntimeException(String.format(
                    "Transformation was applied to an incorrect request format! %s", request.getAbsoluteUrl()));
        }

        //Get the arguments from the query and put them in a map
        final var queryArgs = queryParts[1].split("&");
        final var queryMap = Arrays.stream(queryArgs).map(i -> { //NOPMD
            final var parts = i.split("=");
            return new AbstractMap.SimpleEntry<>(parts[0], parts[1]);
        }).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        //Select all matching artifacts for the received arguments
        final var matchingArtifacts = data.entrySet().stream()
                .filter(i -> i.getKey().contains(Optional.ofNullable(queryMap.get("q")).orElse(i.getKey())))
                .sorted(Map.Entry.comparingByKey())
                .limit(Optional.ofNullable(queryMap.get(RowsProperty.NAME)).map(Long::parseLong).orElse(0L))
                .map(Map.Entry::getValue)
                .map(MockArtifact::toDocsEntry)
                .collect(Collectors.toList());
        //Build a new response which contains the matching packages
        return new ResponseDefinitionBuilder()
                .withStatus(200)
                .withBody(toJson(new MvnResponse(new Response(matchingArtifacts.size(), matchingArtifacts))))
                .build();
    }

    /**
     * @return identifier of the transformed which can be used for stubbing
     */
    @Override
    public String getName() {
        return MockArtifactMap.class.getName();
    }

    /**
     * @return indicates if the transformer should be applied to all stubbing automatically
     */
    @Override
    public boolean applyGlobally() {
        return false;
    }

    private <T> String toJson(final T object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Could not write json! %s", e.getMessage()), e);
        }
    }
}
