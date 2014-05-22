package net.nemerosa.ontrack.model.support;

public interface Configuration<T extends Configuration<T>> {

    String getName();

    /**
     * Utility method that erases any sensitive data from this configuration
     * and returns new one.
     */
    T obfuscate();

}
