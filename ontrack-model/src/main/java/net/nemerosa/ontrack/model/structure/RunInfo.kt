package net.nemerosa.ontrack.model.structure

/**
 * Contains information about the CI run for a
 * [validation run][ValidationRun] or a
 * [build][Build].
 *
 * @property id Unique ID of the run info
 * @property sourceType Type of source (like "jenkins")
 * @property sourceUri URI to the source of the run (like the URL to a Jenkins job)
 * @property triggerType Type of trigger (like "scm" or "user")
 * @property triggerData Data associated with the trigger (like a user ID or a commit)
 * @property runTime Time of the run (in seconds)
 */
open class RunInfo(
        val id: Int,
        val sourceType: String?,
        val sourceUri: String?,
        val triggerType: String?,
        val triggerData: String?,
        val runTime: Int?,
        val signature: Signature?
) {
    val empty = id == 0

    companion object {
        fun empty() = RunInfo(
                id = 0,
                sourceType = null,
                sourceUri = null,
                triggerType = null,
                triggerData = null,
                runTime = null,
                signature = null
        )
    }
}

class RunInfoInput(
        val sourceType: String? = null,
        val sourceUri: String? = null,
        val triggerType: String? = null,
        val triggerData: String? = null,
        val runTime: Int? = null
)
