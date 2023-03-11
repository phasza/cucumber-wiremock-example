package phasza.java.cucumber.example.test.context;

import com.github.tomakehurst.wiremock.WireMockServer;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import phasza.java.cucumber.example.test.data.MockArtifact;

import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Implementation of the mock MVN repository with e.g. mocking responses of artifact queries.
 * Underneath there is a wiremock server which is wrapped with higher level functionality.
 */
@Singleton
@RequiredArgsConstructor
public final class MockMvnRepository implements AutoCloseable {

    /**
     * Instance of the stub transformer which holds configured artifacts
     */
    private final MockArtifactMap mockArtifactMap;
    /**
     * Instance of the wiremock server
     */
    private WireMockServer server;

    /**
     * @return Lazy initialized wiremock server, configured for tests
     */
    public WireMockServer getServer() {
        if (server == null) {
            server = new WireMockServer(options().extensions(mockArtifactMap).dynamicPort());
            server.start();
        }
        return server;
    }

    /**
     * All requests will be answered with 404
     */
    public void setServerDown() {
        getServer().stubFor(any(anyUrl()).atPriority(1).willReturn(notFound()));
    }

    /**
     * "uploads" a mocked artifact to the mock server
     * @param mockArtifact Data to upload
     */
    public void uploadArtifact(final MockArtifact mockArtifact) {
        mockArtifactMap.put(mockArtifact.getId(), mockArtifact);
    }

    /**
     * Finishes the stubbing of possible HTTP requests according to the configured packages.
     * Should be called before starting the application under test.
     */
    public void finishStubbing() {
        getServer().stubFor(get(urlMatching("/solrsearch/select\\?.*")).willReturn(
                aResponse().withStatus(200).withTransformers(MockArtifactMap.class.getName())
        ));
    }

    @Override
    public void close() {
        Optional.ofNullable(server).ifPresent(WireMockServer::stop);
    }
}
