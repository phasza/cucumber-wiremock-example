package phasza.java.cucumber.example.lib.request;

import lombok.Value;

/**
 * Implementation of the "rows" property, which limits the displayed results
 */
@Value
public class RowsProperty implements MvnURLProperty {

    /**
     * Name of the property as in the URL
     */
    public static final String NAME = "rows";
    /**
     * Value of the property
     */
    int value;

    @Override
    public String get() {
        return String.format("%s=%s", NAME, getValue());
    }
}
