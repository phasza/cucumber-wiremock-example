package phasza.java.cucumber.example.app;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import phasza.java.cucumber.example.lib.MvnConnector;
import picocli.CommandLine;


/**
 * Implements the "search" command
 */
@Singleton
@CommandLine.Command(name = "search")
@RequiredArgsConstructor
public class SearchCommand implements Runnable {

    /**
     * --head option
     */
    @CommandLine.Option(
            names = {"-h", "--head"},
            defaultValue = "100",
            description = "Limits the number of results displayed. (Default: 100)")
    private int head;

    /**
     * --url option
     */
    @CommandLine.Option(
            names = "--url",
            defaultValue = "https://search.maven.org",
            description = "URL to maven repository.")
    private String repositoryURL;

    /**
     * Pattern parameter
     */
    @CommandLine.Parameters
    private String pattern;

    /**
     * Injected connector
     */
    private final MvnConnector mvnConnector;

    @Override
    public void run() {
        mvnConnector.runQuery(repositoryURL, pattern, head);
    }
}
