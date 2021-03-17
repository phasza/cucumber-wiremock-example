package phasza.java.cucumber.example.test.context;

import org.slf4j.Logger;
import org.slf4j.Marker;

import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Mock logger used to redirect the log messages from the application
 * for us to be able to test he whole application "end-to-end".
 * This way there is no need to inject an application context to the application under test
 * and we can test the injection as well.
 */
@Singleton
@SuppressWarnings("PMD")
public class TestLogger implements Logger {

    /**
     * Container for stdout
     */
    private final ConcurrentLinkedQueue<String> standardOut = new ConcurrentLinkedQueue<>();
    /**
     * Container for stderr
     */
    private final ConcurrentLinkedQueue<String> standardError = new ConcurrentLinkedQueue<>();

    @Override
    public String getName() {
        return "testLogger";
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return false;
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
        standardOut.addAll(Arrays.asList(msg.split("\n").clone()));
    }

    @Override
    public void info(String format, Object arg) {
        info(String.format(format, arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        info(String.format(format, arg1, arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
        info(String.format(format, arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
        info(msg);
        info(printThrowable(t));
    }

    @Override
    public void info(Marker marker, String msg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg) {

    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {

    }

    @Override
    public void error(String msg) {
        standardError.addAll(Arrays.asList(msg.split("\n").clone()));
    }

    @Override
    public void error(String format, Object arg) {
        error(String.format(format, arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        error(String.format(format, arg1, arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        error(String.format(format, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        error(msg);
        error(printThrowable(t));
    }

    @Override
    public void error(Marker marker, String msg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg) {

    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return false;
    }

    @Override
    public void warn(String msg) {
        this.info(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        this.info(format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        this.info(format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        this.info(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        this.info(msg, t);
    }

    @Override
    public void warn(Marker marker, String msg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg) {

    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {

    }

    @Override
    public void trace(String msg) {

    }

    @Override
    public void trace(String format, Object arg) {

    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(String format, Object... arguments) {

    }

    @Override
    public void trace(String msg, Throwable t) {

    }

    @Override
    public void trace(Marker marker, String msg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {

    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {

    }

    @Override
    public void debug(String msg) {

    }

    @Override
    public void debug(String format, Object arg) {

    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {

    }

    @Override
    public void debug(String format, Object... arguments) {

    }

    @Override
    public void debug(String msg, Throwable t) {

    }

    @Override
    public void debug(Marker marker, String msg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg) {

    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {

    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {

    }

    private String printThrowable(Throwable t) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out)) {
            t.printStackTrace(writer);
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return standard out collected so far
     */
    public List<String> getStandardOutput() {
        return new ArrayList<>(standardOut);
    }

    /**
     * @return standard error collected so far
     */
    public List<String> getStandardError() {
        return new ArrayList<>(standardError);
    }
}
