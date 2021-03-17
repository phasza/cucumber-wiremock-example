package phasza.java.cucumber.example.test.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import phasza.java.cucumber.example.app.Application;
import phasza.java.cucumber.example.app.Status;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for the state of the application under test
 */
@Singleton
@RequiredArgsConstructor
public final class ApplicationContext {

    /**
     * Test logger instance
     */
    private final TestLogger logger;
    /**
     * Extra options which are added after the command line arguments (e.g. URL)
     */
    private final List<String> options = new ArrayList<>();

    /**
     * Value returned by the application under test
     */
    @Getter
    private int status = Status.UNKNOWN.getValue();

    /**
     * Adds an extra option to the option list.
     * The extra options will be added to the end of the arguments.
     * @param option Extra option
     */
    public void addOption(final String option) {
        options.add(option);
    }

    /**
     * Runs a command on a new instance of the application
     * @param args command to run
     */
    public void run(final List<String> args) {
        final var finalArgs = new ArrayList<String>();
        finalArgs.addAll(args);
        finalArgs.addAll(options);
        status = new Application(logger).execute(finalArgs.toArray(new String[0]));
    }

    /**
     * @return standard output
     */
    public List<String> getStandardOutput() {
        return logger.getStandardOutput();
    }

    /**
     * @return standard error
     */
    public List<String> getStandardError() {
        return logger.getStandardError();
    }
}
