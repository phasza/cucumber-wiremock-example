package phasza.java.cucumber.example.lib.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

/**
 * JSON object for the MVN response's [response] part, which contains the
 * number of results found as well as the matching artifacts for the query.
 * Not interesting fields are ignored.
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    /**
     * Number of artifacts found for the query
     */
    int numFound;
    /**
     * List of displayed entries. The server limits the number of elements which
     * are returned by the query. The 'rows' property controls this limit.
     */
    List<DocsEntry> docs;
}
