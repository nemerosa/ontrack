package net.nemerosa.ontrack.extension.indicators.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.indicators.model.IndicatorStatus
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.Signature

class ProjectIndicator(
        val project: Project,
        val type: ProjectIndicatorType,
        val value: JsonNode,
        val status: IndicatorStatus?,
        val comment: String?,
        val signature: Signature
)