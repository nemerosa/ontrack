package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunnableEntityType

interface RunInfoRepository {
    /**
     * Gets the [RunInfo] associated with a runnable entity defined
     * by its [type][RunnableEntityType] and [ID][id].
     */
    fun getRunInfo(runnableEntityType: RunnableEntityType, id: Int): RunInfo

    /**
     * Sets a [run info][RunInfoInput] on a runnable entity defined
     * by its [type][RunnableEntityType] and [ID][id] and returns
     * a created or update [RunInfo].
     */
    fun setRunInfo(runnableEntityType: RunnableEntityType, id: Int, input: RunInfoInput): RunInfo
}
