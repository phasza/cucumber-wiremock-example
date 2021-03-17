package phasza.java.cucumber.example.lib.request;

import lombok.Getter;

/**
 * Implements the 'wt' property, which defines the API output format (XML or JSON)
 */
public final class WTProperty implements MvnURLProperty {

    /**
     * Name of the property in the URL
     */
    public static final String NAME = "wt";
    /**
     * Type value of the property
     */
    @Getter
    private final Type type;

    /**
     * New property from type
     * @param type type
     */
    private WTProperty(final Type type) {
        this.type = type;
    }

    /**
     * @return JSON typed property
     */
    public static WTProperty json() {
        return new WTProperty(Type.JSON);
    }

    @Override
    public String get() {
        return String.format("%s=%s", NAME, getType().getLabel());
    }

    /**
     * Represents the type value for the 'wt' property
     */
    private enum Type {
        JSON("json");

        /**
         * label which describes this type in an URL
         */
        @Getter
        private final String label;

        Type(final String label) {
            this.label = label;
        }
    }
}
