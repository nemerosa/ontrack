package net.nemerosa.ontrack.extension.indicators.model

/**
 * [Source][IndicatorSource] provider
 */
interface IndicatorSourceProvider {

    /**
     * Display name
     */
    val name: String

}

/**
 * ID of an [IndicatorSourceProvider] is its FQCN.
 */
val IndicatorSourceProvider.id: String get() = this::class.java.name

/**
 * Indicator source provider description
 */
data class IndicatorSourceProviderDescription(
        val id: String,
        val name: String
)

val IndicatorSourceProvider.description get() = IndicatorSourceProviderDescription(id, name)

/**
 * Source of a [category][IndicatorCategory] or a [type][IndicatorType].
 */
data class IndicatorSource(
        /**
         * Source provider
         */
        val provider: IndicatorSourceProviderDescription,
        /**
         * Display name
         */
        val name: String
)

/**
 * Creates a source
 */
fun IndicatorSourceProvider.createSource(name: String) = IndicatorSource(
        description,
        name
)
