package net.nemerosa.ontrack.common;

/**
 * List of Spring profiles.
 */
public interface RunProfile {

    /**
     * Development mode.
     */
    String DEV = "dev";

    /**
     * Acceptance mode
     */
    String ACC = "acc";

    /**
     * Production mode
     */
    String PROD = "prod";

}
