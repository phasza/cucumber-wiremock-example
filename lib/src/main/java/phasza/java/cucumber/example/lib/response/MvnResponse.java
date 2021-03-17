package phasza.java.cucumber.example.lib.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * Main class of the MVN response JSON.
 * The returned JSON contains three parts (responseHeader, response, spellcheck),
 * but for simplicity we ignore the fields which are not interesting for us.
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MvnResponse {
    /**
     * Wrapper of the response part
     */
    Response response;
}
