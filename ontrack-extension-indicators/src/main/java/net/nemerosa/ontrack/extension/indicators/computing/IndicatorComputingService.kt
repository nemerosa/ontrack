package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.model.IndicatorComputer
import net.nemerosa.ontrack.model.structure.Project

interface IndicatorComputingService {

    fun compute(computer: IndicatorComputer, project: Project)

}