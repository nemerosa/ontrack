package net.nemerosa.ontrack.extension.indicators.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.ID

interface ProjectIndicatorService {
    fun getProjectIndicators(projectId: ID, all: Boolean): ProjectIndicators

    fun getUpdateFormForIndicator(projectId: ID, typeId: String): Form

    fun updateIndicator(projectId: ID, typeId: String, input: JsonNode): ProjectIndicator

    fun deleteIndicator(projectId: ID, typeId: String): Ack

    fun getPreviousIndicator(projectIndicator: ProjectIndicator): ProjectIndicator

}