package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.structure.ID

interface ProjectIndicatorService {
    fun getProjectIndicators(projectId: ID, all: Boolean): ProjectIndicators

    fun getUpdateFormForIndicator(projectId: ID, typeId: Int): Form

}