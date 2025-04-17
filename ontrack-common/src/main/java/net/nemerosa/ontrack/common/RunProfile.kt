package net.nemerosa.ontrack.common

/**
 * List of Spring profiles.
 *
 * /!\ Do not add other profiles since we consider only DEV or PROD and
 * existing components rely on this duality.
 */
object RunProfile {

    /**
     * Development mode.
     */
    const val DEV = "dev"

    /**
     * Production mode
     */
    const val PROD = "prod"

}
