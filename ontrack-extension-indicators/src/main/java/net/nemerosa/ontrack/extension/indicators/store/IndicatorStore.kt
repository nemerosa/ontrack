package net.nemerosa.ontrack.extension.indicators.store

import net.nemerosa.ontrack.model.structure.Project

interface IndicatorStore {

    fun loadIndicator(project: Project, type: Int): StoredIndicator?

}