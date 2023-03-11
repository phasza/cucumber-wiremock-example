package phasza.java.cucumber.example.test.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import phasza.java.cucumber.example.app.Status;
import phasza.java.cucumber.example.test.context.MockMvnRepository;
import phasza.java.cucumber.example.test.context.ApplicationContext;
import phasza.java.cucumber.example.test.data.MockArtifact;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

/**
 * Container which implements the steps of a "search" feature scenario.
 */
@Singleton
@AllArgsConstructor
public class SearchSteps {

    /**
     * Current application context
     */
    private final ApplicationContext applicationContext;
    /**
     * Mocked MVN repository
     */
    private final MockMvnRepository mockMvnRepository;

    /**
     * Print some helpful messages on failure
     * @param scenario Scenario
     */
    @After
    public void afterScenario(final Scenario scenario) {
        if (scenario.isFailed()) {
            scenario.log(String.format("Test %s failed!", scenario.getId()));
            scenario.log("Standard Out:");
            scenario.log(String.join(System.lineSeparator(), applicationContext.getStandardOutput()));
            scenario.log("");
            scenario.log("Standard Error:");
            scenario.log(String.join(System.lineSeparator(), applicationContext.getStandardError()));
            scenario.log("");
            scenario.log("Missed HTTP Requests:");
            scenario.log(mockMvnRepository.getServer().getServeEvents().getRequests().stream()
                    .filter(i -> !i.getWasMatched())
                    .map(i -> i.getRequest().getUrl())
                    .collect(Collectors.joining(System.lineSeparator())));
        }
    }

    /**
     * Background which ramps up a scenario
     */
    @Given("an installation of the application")
    public void anInstallationOfTheApplication() {
        //This step can be used to setUp the test case
    }

    /**
     * Background which ramps up the MVN server
     */
    @Given("a maven server is available")
    public void mavenServerIsAvailable() {
        //This step can e used to setUP the mock maven server
    }

    /**
     * Background when the MVN server is not responding
     */
    @Given("the maven server is down")
    public void mavenServerIsDown() {
        mockMvnRepository.setServerDown();
    }

    /**
     * @param data Must be in | group | artifact | latestVersion| format
     */
    @Given("the following artifacts are uploaded to the maven server")
    public void followingArtifactsAreUploadedToTheMavenServer(final DataTable data) {
        for (final var entry : data.asLists()) {
            mockMvnRepository.uploadArtifact(new MockArtifact(entry.get(0), entry.get(1), entry.get(2))); //NOPMD
        }
        mockMvnRepository.finishStubbing();
    }

    /**
     * @param command Can start with "mvn-query" for better readability, but this can be ommitted
     */
    @When("I run {string}")
    public void whenRunCommand(final String command) {
        applicationContext.addOption(String.format("--url=%s", mockMvnRepository.getServer().baseUrl()));
        final var args = Arrays.stream(command.split(" "))
                .filter(i -> !i.isBlank())
                .collect(Collectors.toList());
        args.remove("mvn-query");
        applicationContext.run(args);
    }

    /**
     * Asserts that the command was successful
     */
    @Then("the command returns with success")
    public void commandReturnsWithSuccess() {
        assertEquals(
                Status.SUCCESS.getValue(),
                applicationContext.getStatus(),
                "Command did not return with SUCCESS");
    }

    /**
     * Asserts that the command was erroneous
     */
    @Then("the command returns with error")
    public void commandReturnsWithError() {
        assertEquals(
                Status.ERROR.getValue(),
                applicationContext.getStatus(),
                "Command did not return with ERROR");
    }

    /**
     * @param expected Expected lines which should be present in the output
     */
    @Then("the following lines are included in the standard output")
    public void followingLinesAreIncludedInTheStandardOutput(final List<String> expected) {
        assertLinesMatch(expected, applicationContext.getStandardOutput());
    }

    /**
     * @param expected Expected lines which should be present in the error output
     */
    @Then("the following lines are included in the standard error")
    public void followingLinesAreIncludedInTheStandardError(final List<String> expected) {
        assertLinesMatch(expected, applicationContext.getStandardError());
    }
}
