package net.nemerosa.ontrack.extension.indicators.model

/**
 * Access to the [indicator sources][IndicatorSource] and their [providers][IndicatorSourceProvider].
 */
interface IndicatorSourceService {

    /**
     * Gets an [indicator source provider][IndicatorSourceProvider] using its ID.
     */
    fun findIndicatorSourceProviderById(id: String): IndicatorSourceProvider?

}
