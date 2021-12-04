package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

abstract class AbstractPRPayloadListener(
    val action: PRPayloadAction,
) : PRPayloadListener {

    override fun preProcessCheck(payload: PRPayload): PRPayloadListenerCheck =
        when {
            !payload.pullRequest.sameRepo() -> PRPayloadListenerCheck.IGNORED
            payload.action == action -> PRPayloadListenerCheck.TO_BE_PROCESSED
            else -> PRPayloadListenerCheck.IGNORED
        }

}