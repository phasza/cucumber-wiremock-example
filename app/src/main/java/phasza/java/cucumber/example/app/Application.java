/*
 * This Java source file was generated by the Gradle "init" task.
 */
package phasza.java.cucumber.example.app;


import com.fasterxml.jackson.databind.json.JsonMapper;
import io.micronaut.configuration.picocli.MicronautFactory;
import io.micronaut.context.ApplicationContext;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Main entry point of the application.
 * If used from the CLI, the main(...) method creates the default logger
 * If used from integration test, then it uses the injected logger
 * Then creates the app context, builds the command line and executed the command.
 */
@Singleton
@CommandLine.Command(
        name = "mvn-query",
        subcommands = {
                SearchCommand.class
        },
        versionProvider = VersionProvider.class)
@RequiredArgsConstructor
public class Application implements Callable<Integer> {

    /**
     * --version option
     */
    @CommandLine.Option(
            names = {"-v", "--version"},
            versionHelp = true,
            description = "Displays the version")
    private boolean version;

    /**
     * --help option
     */
    @CommandLine.Option(
            names = {"-h", "--help"},
            usageHelp = true,
            description = "Displays the help")
    private boolean help;

    /**
     * Help message is "rendered" runtime, we need to save its value to print it
     * if the app is used without parameters.
     */
    private String helpMessage;

    /**
     * Injected logger
     */
    private final Logger logger;

    /**
     * Executes the given arguments from the CLI
     * !!!IMPORTANT!!! Calls System.exit to give back a return value
     * do not call this from a test JVM instance, it will kill the JVM immediately
     * @param args Arguments to execute
     */
    public static void main(final String... args) {
        System.exit(new Application(LoggerFactory.getLogger(Application.class.getName())).execute(args)); //NOPMD
    }

    /**
     * Implementation of the command execution, used both from the CLI and from test
     * @param args args to executed
     * @return Status of the command
     */
    public int execute(final String... args) {
        try (var context = ApplicationContext
                .builder(Application.class)
                .singletons(createSingletons(this))
                .start()) {
            final var commandLine = new CommandLine(this, new MicronautFactory(context))
                    .setTrimQuotes(true)
                    .setUsageHelpWidth(100)
                    .setUsageHelpAutoWidth(true)
                    .setExecutionExceptionHandler((ex, commandLine1, parseResult) -> logException(ex));
            helpMessage = commandLine.getUsageMessage();
            return commandLine.execute(args);
        } catch (Exception e) { //NOPMD
            logger.error(e.getMessage(), e);
            return Status.ERROR.getValue();
        }
    }

    private int logException(final Exception e) {
        logger.debug(e.getMessage(), e);
        logger.error(e.getMessage());
        return Status.ERROR.getValue();
    }

    /**
     * Registers all classes which are not configured with the javax Singleton annotation
     * but are used as singletons
     * @param application Instance of this app
     * @return Array of singleton
     */
    private Object[] createSingletons(final Application application) {
        return new Object[]{
                application,
                logger,
                new JsonMapper()
        };
    }

    /**
     * Runs where there were no arguments given.
     * Displays the help message
     * @return Always success
     */
    @Override
    public Integer call() {
        logger.info(helpMessage);
        return Status.SUCCESS.getValue();
    }
}
