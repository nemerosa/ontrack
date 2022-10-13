package net.nemerosa.ontrack.extension.chart

import kotlin.reflect.KClass

/**
 * Registry of chart providers.
 */
interface ChartRegistry {

    /**
     * Gets the list of providers for a given subject class
     */
    fun <S : Any> getProvidersForSubjectClass(type: KClass<S>): List<ChartProvider<S, *, *>>

    /**
     * Gets a provider by name or returns null if not found
     */
    fun <S : Any, T : Any, C : Chart> findProvider(name: String): ChartProvider<S, T, C>?

}