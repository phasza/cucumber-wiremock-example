package phasza.java.cucumber.example.test;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.cucumber.core.backend.ObjectFactory;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.core.reflect.InstantiationUtils;
import lombok.NoArgsConstructor;
import phasza.java.cucumber.example.app.Application;

import java.util.Optional;

/**
 * Factory for instantiating test object during a test life-cycle.
 * This factory replaces the default cucumber object factory with a micronaut factory.
 * (cucumber has its built-in dependency injection)
 * The default factory is overridden by the "/resources/META-INF.service/io.cucumber.core.backend.ObjectFactory"
 * descriptor, where you need to point to the ObjectFactory class
 */
@NoArgsConstructor
public final class TestObjectFactory implements ObjectFactory {

    /**
     * Context of the current scenario
     */
    private ApplicationContext context;

    /**
     * Starts the scenario context by creating a new context
     */
    @Override
    public void start() {
        context = ApplicationContext
                .builder(Application.class, Environment.TEST)
                .singletons(new JsonMapper())
                .start();
    }

    /**
     * Closes the scenario context
     */
    @Override
    public void stop() {
        context.close();
    }

    /**
     * @param someClass Class to add
     * @return Return true on trying to add a class always, as the re-instantiating
     * is handled in the getInstance method
     */
    @Override
    public boolean addClass(final Class<?> someClass) {
        return true;
    }

    /**
     * Returns a new instance of the required class if needed, or an already existing instance
     * @param clazz Class to instantiate
     * @param <T> Type of the class
     * @return Instantiated object
     */
    @Override
    public <T> T getInstance(final Class<T> clazz) {
        final Optional<T> bean = context.findBean(clazz);
        return bean.orElseGet(() -> InstantiationUtils.instantiate(clazz));
    }
}
