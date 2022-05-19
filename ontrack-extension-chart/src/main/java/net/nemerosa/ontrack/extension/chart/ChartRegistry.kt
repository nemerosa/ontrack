package net.nemerosa.ontrack.extension.chart

/**
 * Registry of chart providers.
 */
interface ChartRegistry {

    /**
     * Gets a provider by name or returns null if not found
     */
    fun <T: Any, C: Chart> findProvider(name: String): ChartProvider<T, C>?

}