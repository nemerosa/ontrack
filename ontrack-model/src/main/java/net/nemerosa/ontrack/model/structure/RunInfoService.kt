package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.Ack

/**
 * Management of [RunInfo] for [Build]s
 * and [ValidationRun]s.
 */
interface RunInfoService {
    /**
     * Loads the [runnable entity][RunnableEntity] defined by
     * its [type][RunnableEntityType] and [id].
     */
    fun getRunnableEntity(runnableEntityType: RunnableEntityType, id: Int): RunnableEntity

    /**
     * Gets the [RunInfo] associated with a [runnable entity][RunnableEntity].
     */
    fun getRunInfo(entity: RunnableEntity): RunInfo?

    /**
     * Sets a [run info][RunInfoInput] on a [runnable entity][RunnableEntity] and returns
     * a created or update [RunInfo].
     */
    fun setRunInfo(entity: RunnableEntity, input: RunInfoInput): RunInfo

    /**
     * Deletes any existing [RunInfo] associated with [runnableEntity].
     *
     * @return [Ack.OK] if the the [RunInfo] was existing, [Ack.NOK] otherwise.
     */
    fun deleteRunInfo(runnableEntity: RunnableEntity): Ack
}