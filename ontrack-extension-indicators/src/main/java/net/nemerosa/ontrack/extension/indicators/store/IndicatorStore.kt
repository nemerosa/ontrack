package net.nemerosa.ontrack.extension.indicators.store

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.Project
import java.time.Duration

interface IndicatorStore {

    fun loadIndicator(project: Project, type: String, previous: Duration? = null): StoredIndicator?

    fun storeIndicator(project: Project, type: String, indicator: StoredIndicator)

    fun deleteIndicator(project: Project, typeId: String): Ack

    fun deleteIndicatorByType(typeId: String)

}