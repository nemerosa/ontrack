package net.nemerosa.ontrack.extension.license

/**
 * Provides access to the current license.
 */
interface LicenseService {

    /**
     * Gets the current license.
     */
    val license: License

}