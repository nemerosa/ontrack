package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.model.structure.Project

class ProjectCategoryIndicators(
        val project: Project,
        val category: IndicatorCategory,
        val indicators: List<ProjectIndicator>
)