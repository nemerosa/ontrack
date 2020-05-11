package net.nemerosa.ontrack.extension.indicators.store

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.Project

interface IndicatorStore {

    fun loadIndicator(project: Project, type: String): StoredIndicator?

    fun storeIndicator(project: Project, type: String, indicator: StoredIndicator)

    fun deleteIndicator(project: Project, typeId: String): Ack

}