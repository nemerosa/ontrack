package net.nemerosa.ontrack.model.structure

import org.springframework.stereotype.Component

/**
 * Records events about run infos, so that for example they can
 * be exported in other systems.
 */
interface RunInfoListener {

    /**
     * Event fired when an run info is created.
     *
     * @param runnableEntity Entity the run info is created for
     * @param runInfo Run info being created
     */
    fun onRunInfoCreated(
            runnableEntity: RunnableEntity,
            runInfo: RunInfo
    )

}

/**
 * NOP implementation to satisfy Spring.
 */
@Component
class NOPRunInfoListener : RunInfoListener {
    override fun onRunInfoCreated(runnableEntity: RunnableEntity, runInfo: RunInfo) {
    }
}