package net.nemerosa.ontrack.common;

/**
 * List of Spring profiles.
 */
public interface RunProfile {

    /**
     * Unit test mode
     */
    String UNIT_TEST = "unitTest";

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
