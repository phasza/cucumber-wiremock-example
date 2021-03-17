package phasza.java.cucumber.example.test.data;

import lombok.Value;
import phasza.java.cucumber.example.lib.response.DocsEntry;

/**
 * Object which is described by the datatable used to define uploaded artifacts on the
 * mock MVN repository
 */
@Value
public class MockArtifact {
    /**
     * GroupID
     */
    String group;
    /**
     * ArtifactID
     */
    String artifact;
    /**
     * Latest Version
     */
    String latestVersion;

    /**
     * @return returns the [groupID]:[artifactID] id
     */
    public String getId() {
        return String.format("%s:%s", getGroup(), getArtifact());
    }

    /**
     * @return A response docs entry from this mock artifact object, used for stubbing
     */
    public DocsEntry toDocsEntry() {
        return new DocsEntry(
                getId(),
                getGroup(),
                getArtifact(),
                getLatestVersion(),
                1);
    }
}
