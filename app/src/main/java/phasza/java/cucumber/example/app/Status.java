package phasza.java.cucumber.example.app;

import lombok.Getter;

/**
 * Represents the return code or 'status' of the application
 */
public enum Status {
    ERROR(1),
    SUCCESS(0),
    UNKNOWN(Integer.MAX_VALUE);

    /**
     * Value of the status as integer
     */
    @Getter
    private final int value;

    Status(final int value) {
        this.value = value;
    }
}
