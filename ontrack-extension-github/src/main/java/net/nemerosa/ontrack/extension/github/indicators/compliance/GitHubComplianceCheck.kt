package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorAttribute
import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorState
import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType
import net.nemerosa.ontrack.model.structure.Project

interface GitHubComplianceCheck<T, C> {

    val category: IndicatorComputedCategory
    val id: String
    val name: String
    val attributes: List<ConfigurableIndicatorAttribute>
    val valueType: IndicatorValueType<T, C>
    val valueConfig: (project: Project, state: ConfigurableIndicatorState) -> C
    val computing: (project: Project, state: ConfigurableIndicatorState) -> T?

}