package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.model.IndicatorSource
import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Project

/**
 * Computation of indicators.
 */
interface IndicatorComputer : Extension {

    /**
     * Display name of the computation
     */
    val name: String

    /**
     * Computation parallelisation
     *
     * If `true`, one job per project, if `false`, one job for all projects
     */
    val perProject: Boolean

    /**
     * Scheduling of the collection.
     *
     * Once a day by default.
     */
    val schedule: Schedule get() = Schedule.EVERY_DAY

    /**
     * Associated source
     */
    val source: IndicatorSource

    /**
     * Computing all indicators for the [project]. This includes not only the
     * values but the associated categories & types.
     *
     * Note that ALL categories
     * and types managed by this indicator computer must be provided because
     * they will be used to provision the repository of categories & types,
     * after having been associated with the [source].
     */
    fun computeIndicators(project: Project): List<IndicatorComputedValue<*, *>>

    /**
     * Is a project eligible for those computed indicators?
     */
    fun isProjectEligible(project: Project): Boolean

    /**
     * List of configurable indicators for this computer.
     */
    val configurableIndicators: List<ConfigurableIndicatorType<*,*>> get() = emptyList()

}

/**
 * The ID of an [IndicatorComputer] is its FQCN.
 */
val IndicatorComputer.id: String get() = this::class.java.name

