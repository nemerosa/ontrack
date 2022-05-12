package net.nemerosa.ontrack.extension.chart

/**
 * Registry of chart providers.
 */
interface ChartRegistry {

    /**
     * Gets a provider by name or returns null if not found
     */
    fun <T: Any> findProvider(name: String): ChartProvider<T>?

}