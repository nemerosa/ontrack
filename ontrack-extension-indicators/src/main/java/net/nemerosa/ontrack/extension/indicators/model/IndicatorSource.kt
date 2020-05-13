package net.nemerosa.ontrack.extension.indicators.model

/**
 * Source of a [category][IndicatorCategory] or a [type][IndicatorType].
 */
interface IndicatorSource {

    /**
     * Display name
     */
    val name: String

}

/**
 * ID of an [IndicatorSource] is its FQCN.
 */
val IndicatorSource.id: String get() = this::class.java.name
