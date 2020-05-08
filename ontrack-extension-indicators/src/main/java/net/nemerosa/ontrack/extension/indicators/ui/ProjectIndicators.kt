package net.nemerosa.ontrack.extension.indicators.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorStatus
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature

interface ProjectIndicatorService {
    fun getProjectIndicators(projectId: ID, all: Boolean): ProjectIndicators
}

class ProjectIndicators(
        val project: Project,
        val categories: List<ProjectCategoryIndicators>
)

class ProjectCategoryIndicators(
        val category: IndicatorCategory,
        val indicators: List<ProjectIndicator>
)

class ProjectIndicator(
        val type: ProjectIndicatorType,
        val value: JsonNode,
        val status: IndicatorStatus?,
        val comment: String?,
        val signature: Signature
)

class ProjectIndicatorType(
        val id: Int,
        val name: String
) {
    constructor(type: IndicatorType<out Any?, out Any?>) : this(
            id = type.id,
            name = type.longName
    )
}