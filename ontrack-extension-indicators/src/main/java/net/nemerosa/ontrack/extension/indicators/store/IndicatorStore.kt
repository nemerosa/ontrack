package net.nemerosa.ontrack.extension.indicators.store

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.Project
import java.time.Duration

interface IndicatorStore {

    fun loadIndicator(project: Project, type: String, previous: Duration? = null): StoredIndicator?

    fun loadIndicatorHistory(project: Project, type: String, offset: Int = 0, size: Int = Int.MAX_VALUE): List<StoredIndicator>

    fun storeIndicator(project: Project, type: String, indicator: StoredIndicator)

    fun deleteIndicator(project: Project, typeId: String): Ack

    fun deleteIndicatorByType(typeId: String)

    /**
     * Gets the _previous_ value for the indicator of a given [project] and [type][typeId].
     *
     * @param project Project
     * @param typeId ID of the indicator type
     * @param offset History offset
     * @param size History size
     * @return `null` if no previous value is available
     */
    fun loadPreviousIndicator(project: Project, typeId: String): StoredIndicator?

    fun getCountIndicatorHistory(project: Project, typeId: String): Int

}