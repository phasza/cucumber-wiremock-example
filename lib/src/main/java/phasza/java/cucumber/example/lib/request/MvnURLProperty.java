package phasza.java.cucumber.example.lib.request;

/**
 * Interface to define properties, which can be used for MVN queries
 */
public interface MvnURLProperty {
    /**
     * @return Formatted property which can be used, as is, in the HTTP requests
     */
    String get();
}
