package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicator
import net.nemerosa.ontrack.model.structure.Project

/**
 * Reporting on indicators.
 */
interface IndicatorReportingService {

    /**
     * Given a filter, returns the list of matching projects.
     *
     * @param filter Filter on projects
     * @param types When the [filled only][IndicatorReportingFilter.filledOnly] parameter is set to `true`, uses
     * this list of types to assert if the project has some indicator value
     * @return Matching projects
     */
    @Deprecated("Use the report method directly")
    fun findProjects(filter: IndicatorReportingFilter, types: List<IndicatorType<*, *>>): List<Project>

    /**
     * Given a filter on projects, returns a report on all the indicators.
     *
     * @param filter Filter on projects
     * @param types When the [filled only][IndicatorReportingFilter.filledOnly] parameter is set to `true`, uses
     * this list of types to assert if the project has some indicator value
     * @return Matrix of projects --> indicators
     */
    fun report(filter: IndicatorReportingFilter, types: List<IndicatorType<*, *>>): IndicatorProjectReport
}