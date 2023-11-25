package net.nemerosa.ontrack.common

/**
 * List of Spring profiles.
 */
object RunProfile {
    /**
     * Unit test mode
     */
    const val UNIT_TEST = "unitTest"

    /**
     * Development mode.
     */
    const val DEV = "dev"

    /**
     * Acceptance mode
     */
    const val ACC = "acceptance"

    /**
     * Production mode
     */
    const val PROD = "prod"

    /**
     * Accessory profile to run with CORS enabled
     */
    const val CORS = "cors"
}
