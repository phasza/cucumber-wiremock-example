package phasza.java.cucumber.example.lib.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Properties of matching artifacts are returned with this JSON object.
 * Not interesting fields are ignored.
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocsEntry {
    /**
     * Identifier of the artifact (normally [group]:[artifact]
     */
    String id;
    /**
     * Group ID
     */
    @JsonProperty("g")
    String group;
    /**
     * Artifact ID
     */
    @JsonProperty("a")
    String artifact;
    /**
     * Latest version found
     */
    String latestVersion;
    /**
     * Number of versions found
     */
    int versionCount;
}
