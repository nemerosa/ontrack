package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.structure.Project

interface IndicatorComputer<T> {
    fun computeIndicator(project: Project): T?
}