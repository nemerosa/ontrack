package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.structure.Project

interface IndicatorService {

    fun getProjectIndicators(project: Project, all: Boolean): List<Indicator<*>>

    fun getProjectIndicator(project: Project, indicatorId: Int): Indicator<*>

}