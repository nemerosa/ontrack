package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunnableEntityType
import net.nemerosa.ontrack.model.structure.Signature

interface RunInfoRepository {
    /**
     * Gets the [RunInfo] associated with a runnable entity defined
     * by its [type][RunnableEntityType] and [ID][id].
     */
    fun getRunInfo(runnableEntityType: RunnableEntityType, id: Int): RunInfo?

    /**
     * Sets a [run info][RunInfoInput] on a runnable entity defined
     * by its [type][RunnableEntityType] and [ID][id] and returns
     * a created or update [RunInfo].
     */
    fun setRunInfo(runnableEntityType: RunnableEntityType, id: Int, input: RunInfoInput, signature: Signature): RunInfo

    /**
     * Deletes any existing [RunInfo] associated with the [type][runnableEntityType] and
     * [id].
     *
     * @return [Ack.OK] if the the [RunInfo] was existing, [Ack.NOK] otherwise.
     */
    fun deleteRunInfo(runnableEntityType: RunnableEntityType, id: Int): Ack
}
