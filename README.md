# Welcome to this cucumber-wiremock-example

As the name of the project suggests, this aims to be a demonstration on how to create a simple application which connects to an external REST service and how to write integration tests (acceptance tests) against the program using cucumber for testing and wiremock for mocking the external service.

## About the example project

The example project implements an application `mvn-query` which has a single command `search`. **The application queries Maven packages from the Maven central server**. 

E.g. `mvn-query search guice -h=100` would return the first 100 artifacts whose ID is matching the `guice` keyword.

The app uses apache HTTP to connect to the maven server (currently https://search.maven.org) and queries the packages via the REST API (https://search.maven.org/classic/#api).

The structure of the project is the following:
* /cucumber-wiremock-example    <- root project
    * /app                      <- implementation of the `mvn-query` application
    * /buildSrc                 <- definition of common gradle conventions (common dependencies etc.)
    * /integration-test         <- cucumber tests for the `app` project
    * /lib                      <- library to connect to the MVN server

Running the application:
```
cd $projectRoot/app
gradle run --args="search gui"
```

## Technologies and frameworks used in this example
* java 11+
* gradle
* cucumber - testing framework
* gherkin - language of cucumber tests
* wiremock - simulator for REST service
* micronaut - full-stack framework
* fasterxml/jackson - java JSON library
* lombok - boiler-plate code generation
* picocli - command line interface

### What is cucumber?

**Cucumber is a testing framework** which allows users to define test cases in plain text, using simple plain english (or french, or german ect. but human readable). Test cases are defined using **gherkin**, which is the syntax of the cucumber tests, a set of rules and keywords which gives a structure text so it becomes an executable specification for the test framework. Cucumber has binding to several programming languages (amongst them java).

Essentially a test case looks like the following:
```gherkin
Feature: Ping command
  The app can ping a website

  Background:
    Given an installation of the application
    And a server is available at "www.google.com"

  Scenario: Ping a website and the server responds
    When I run "ping www.google.com"
    Then the command returns with success   
    And the following lines are included in the standard output
      | PING www.google.com (${ip_matcher}) ${byte_count} bytes of data. |
```
The test case above tests the ping command. It has the following parts:
* Feature: A collection of test cases (scenarios) which belongs to a unit of functionality (feature) in the application under test
* Background: Preconditions (similar to JUnit `@BeforeEach`). The things set here will be the boundary condition for all of scenarios in this feature.
* Scenario: A test case
* Given, When, Then, And and But: Steps in the test which are executed sequentially
    * Given: some precondition
    * When: an event which happens to the application under test (e.g. user input, trigger)
    * Then: an assertion where we validate the state of our application
    * And, But: can replace any of the keywords above to bundle multiple statements for better readability

Underneath we can implement the programmatic part of the instructions like:
```java
@When("^I run {string}$")
public void whenIRunCommand(String command) {
    testContext.setReturnCode(new AppUnderTest().execute(command.split(' ')))
}
``` 
The framework will substitute the `whenIRunCommand` method when the regular expression in the `@When` annotation matches the instruction in the gherkin file. The method will be invoked with a parameter, which is extracted by the `{string}` matcher.

Some pros for using cucumber:
* **supports behavior-driven development**: it is up to you whether you see BDD as a pro or con
* **programming language independent**: you can easily re-implement a micro-service or two in a brand new language keeping the tests intact
* **human readable**: I hate writing requirement specification, also integration tests are not my favorite thing, so why not kill two birds with one stone? Properly written gherkins can be used as requirement specs as non-technical stakeholders can also understand it
* **enforces the same structure for tests**: gherkins are well structured, it is easy to oversee deviations during code-reviews

Some cons:
* **setting up in a project needs some work**: small applications might not worth it
* **can be hard to oversee**: you need to jump back-and-forth between the gherkin and the underlying code to understand
* **can be expensive to maintain**: especially for complex systems
* **tests might not be independent form each other**: if you try to reduce complexity by sharing functionality, backgrounds and steps among features and test cases, you will end up with the fact that changing a background step might affect several features.

### What is wiremock?

Wiremock is a simulator for HTTP-based APIs or simply a **mock server**. Essentially it listens on a given address and port and answers HTTP requests as configured.

e.g. I can configure the wiremock to respond any HTTP POST request to return a status 200 (OK) with an empty body, to mock the upload in my application.

Wiremock can be a standalone process on a remote server, or on the same host as the tested system or can be a library used by a JVM application. We will concentrate on the latter.

### What is Micronaut?

Micronaut is a framework for building micro-service applications in Java, Kotlin or groovy. It uses DI containers which perform dependency injection at compile time, which reduces memory footprint and startup time. Basically you have all the functionality which you need for a desktop application or standalone server application without referencing the whole Spring framework.

I especially like to use Micronaut in combination with picocli and graalVM, because I can create a command line application which is small and fast yet it has the same flexibility as any micro-service compiled into a single JAR.

Moreover picocli commands and subcommands implemented as a micronaut `@Singleton` has the exact same structure as REST API commands.

## Creating a similar project

I'm going to try to give a step-wise guide how to set up a project like this. I assume that you are familiar with gradle projects, if not there are plenty of guides on the web.

> Note: I prefer gradle over maven, because I find it faster, more flexible and better readable but it is up to you to decide of course.

### Creating a micronaut application

Micronaut has a CLI where you can initialize new micronaut applications from templates. Also micronaut has a gradle plugin, which configures the micronaut BOM and sets up annotation processing.
I'm gonna skip that as I find it more educational to do it by hand and understand how the setup works.

1. **Add the dependencies**
```groovy
annotationProcessor 'io.micronaut:micronaut-inject-java:2.4.0'
implementation 'io.micronaut:micronaut-inject:2.4.0'
testAnnotationProcessor 'io.micronaut:micronaut-inject-java:2.4.0'
```
Micronaut beans are configured with `javax.inject` annotations implemented in the micronaut library, so we need micronaut to do the annotation processing and we need the implementation to access the API.

2. **Create an application context**

The ApplicationContext is the main entry point for starting and running Micronaut applications. It can be thought of as a container object for all dependency injected objects. The ApplicationContext can be started via the `run()` method. Or alternatively, the `builder()` method can be used to customize the ApplicationContext using the ApplicationContextBuilder interface prior to running. I prefer the latter as it gives more control.

We need to create this single entry point in our application:
```java
public static void main(String... args) {
    try (var context = ApplicationContext
            .builder(Main.class)
            .start()) {
        System.out.println("Application has been started!")
    }
}
```
The `builder()` needs the main class of the application (I guess it needs it to get the current class loader) and optionally an environment to configure the framework. The default environment is `FUNCTIONAL` so I omitted the environment.

The `start()` method creates the context and finalizes the configuration. We haven't configured much for now.

The `context` should be used in a try-resource or closed manually otherwise as we need to close it to invoke all the `AutoCloseable` beans' `close()` method and free up any resources needed (like releasing a sever connection).


3. **Create a service bean and inject it into the consumer**
Now we need some services to inject. For example here are two test services:
```java
@Singleton
class DBConnector implements AutoClosable {
    DBConnector() {
        //connects to database
    }

    @Override
    public void close() {
        //releases db connection
    }
}

@Singleton
class QueryService {

    private final DBConnector connector;

    @Inject
    QueryService(DBConnector connector) {
        this.connector = connector;
    }

    public List<String> listAllTables() {...}
}
```
The `DBConnector` is a service to connect to some database the first time the service is used and release the database connection when we close the application context.

`QueryService` uses the `DBConnector` to run some queries on the database like listing all data tables. The `DBConnector` is injected into the `QueryService`. Note that the `@Inject` annotation could be omitted and the code would still run, I just put it here for to indicate the injection.

Now we need to run the service in the main function:
```java
try (var context = ApplicationContext
        .builder(Main.class)
        .start()) {
    var qService = context.findOrInstantiateBean(QueryService.class).orElseThrow(
        () -> new RuntimeException("Could not instantiate QueryService bean!"));
    qService.listAllTables().forEach(System.out::println);
}
```
The `findOrInstantiateBean`, as the name suggests, finds an already existing bean or creates a new one returning an `Optional<T>` which is `empty()` if the instantiation was not successful. 

5. **Inject a third-party service, which is not a bean**
If you want to consume a service from a third party library, which is not a micronaut bean you have two options: wrap it in a bean class (or provider etc.), register it as singleton.

Let's assume we want to create a service from the `java.util.Random()` class.
1. **Wrap it in a bean class**
This is pretty straight forward:
```java
@Singleton
class RandomBean {
    @Getter
    private final Random boxed = new Random();
}
```

2. **Register is as singleton**
We can register any number of singletons before issuing the `start()` method of the application context.
```java
public static void main(String... args) {
    try (var context = ApplicationContext
            .builder(Main.class)
            .singletons(new Random())
            .start()) {
        var randomService = context.findOrInstantiateBean(Random.class)
        ...
    }
}
```

### Modifying the application to use picocli

Picocli is a command line interface library for Java, which has basically all the features you will ever need for creating a CLI application. What's even better is that it has a seamless integration with a number of popular java frameworks, micronaut amongst them.

MicronautPicocli defines commands and sub-commands as micronaut beans, which gets instantiated as the picocli command line parser matches the commands and arguments registered for the bean.

e.g.
```java
@Singleton
@CommandLine.Command(name = "ping")
class PingCommand {
    @CommandLine.Parameters
    private String url;
}
```
Is instantiated when the user gives the `app ping www.google.com` command.

1. **Adding the dependencies**
```groovy
implementation 'info.picocli:picocli:4.6.1'
implementation 'io.micronaut.picocli:micronaut-picocli:3.2.0'
```
`picocli` artifact is the library itself, `micronaut-picocli` is the micronaut integration with the picocli framework.

2. **Annotating the commands and sub-commands**
Use the `QueryService` above, we want to achieve the following:
* if the user inputs `app` without parameters we will print hello
* if the user inputs `app query something` we will print something

First lets annotate the main class:
```java
@Singleton
@CommandLine.Command(name = "app")
class App implements Runnable {
    public static void main(String... args) {
        try (var context = ApplicationContext
                .builder(Main.class)
                .start()) {
                    //TODO we will create the command line here later
        }
    }

    @Override
    public void run() {
        System.out.println("hello");
    }
}
```

Then the service class
```java
@Singleton
@CommandLine.Command(name = "query")
@RequiredArgsConstructor
class QueryService implements Runnable {
    private final DBConnector connector;

    @CommandLine.Parameters
    private String param;

    @Override
    public void run() {
        System.out.println(param)
    }
}
```
As you can see, we implemented the `Runnable` interface, as the picocli framework will execute the `run()` command when the associated command is the input. Other possibility is to use a `Callable<T>` interface to provide a result for the command.

3. **Create the command line and its factory**
We need to create and configure the picocli command line to execute when the app runs.
So we need to extend the `main(...)` function.
```java
public static void main(String... args) {
    try (var context = ApplicationContext
            .builder(App.class)
            .start()) {
        new CommandLine(this, new MicronautFactory(context)).execute(args);
    }
}
```
The `CommandLine` will match the given arguments against the annotated commands and parameters and use the `MicronautFactory` to instantiate the necessary beans for the execution through the micronaut framework.

### Create a cucumber test project for the application

Now we will create an integration test project (I prefer this term to acceptance tests as the name, acceptance, indicates that these tests are only necessary for some higher power to accept the code changes).

1. **Create a new project, which depends on the application implementation**
Create a new sub-module and add the implementation project as dependency.
In the example we will use JUnit5 so you will also need to run with jupiter:
```groovy
dependencies {
    ...
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.6.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

2. **Add the cucumber dependencies**
```groovy
testImplementation 'io.cucumber:cucumber-java:6.10.2'
testImplementation 'io.cucumber:cucumber-junit-platform-engine:6.10.2'
```

3. **Create the test runner**
Create a new class under the `src/test/java` called `TestRunner`
```java
@Cucumber
class TestRunner {


}
```
This class is a necessary entry point for the cucumber testing. It can stay empty, but you can implement setup and tear-down functionality in it (e.g. to delete files from the filesystem)

4. **Optional: Overwrite the cucumber DI to use micronaut**
Cucumber has its built-in dependency injection to instantiate implementation classes with its limitation. If you wish to use micronaut instead, then:

* implement the cucumber `ObjectFactory` interface to use micronaut:
```java
public final class TestObjectFactory implements ObjectFactory {

    private ApplicationContext context;

    @Override
    public void start() {
        context = ApplicationContext.builder(Application.class, Environment.TEST).start();
    }

    @Override
    public void stop() {
        context.close();
    }

    /**
     * @return Return true on trying to add a class always, as the re-instantiating is handled in the getInstance method
     */
    @Override
    public boolean addClass(final Class<?> someClass) {
        return true;
    }

    @Override
    public <T> T getInstance(final Class<T> clazz) {
        final Optional<T> bean = context.findBean(clazz);
        return bean.orElseGet(() -> InstantiationUtils.instantiate(clazz));
    }
```
* create a new file: 
`$projectDir/src/test/resources/META-INF/services/io.cucumber.core.backend.ObjectFactory`
* put the class path of the custom `ObjectFactory` into the file e.g.:
`phasza.java.cucumber.example.test.TestObjectFactory`

5. **Write a feature and test cases**
Put the feature files into `$projectDir/src/test/resources/phasza/java/cucumber/example/test/features/Search.feature`, where of course the part after `resources` matches the package where the `TestRunner` class resides, otherwise the cucumber won't see your tests.

Write the feature tests using the gherkin syntax.

6. **Implement the steps**
Implement the steps in steps classes, context similarly to the example project.

### Create a mock server for the tests

1. **Add the wiremock dependencies to the test project**
```groovy
testImplementation 'com.github.tomakehurst:wiremock:2.27.2'
```

2. **Create a mock server service**
An example:
```java
@Singleton
public class MockServer implements AutoClosable {
    private final WireMockServer server;

    public MockServer() {
        server = new WireMockServer(options().dynamicPort());
    }

    @Override
    public void close() {
        server.stop()
    }
}
```
which would fire up a new wiremock server on a dynamically allocated port.
For more info about configuration see the wiremock documentation or the example project

3. **Stub the requests you want**
The variation of stubbing is huge, so check out the example project and the wiremock documentation.