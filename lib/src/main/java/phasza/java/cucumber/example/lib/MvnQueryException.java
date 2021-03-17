package phasza.java.cucumber.example.lib;

/**
 * Any exception during connecting to the Maven server
 */
public final class MvnQueryException extends RuntimeException {

    /**
     * serial version UID
     */
    private static final long serialVersionUID = -5282203405681521516L;

    /**
     * New exception with message
     * @param message message
     */
    public MvnQueryException(final String message) {
        super(message);
    }

    /**
     * New exception with message and cause
     * @param message message
     * @param cause cause
     */
    public MvnQueryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
