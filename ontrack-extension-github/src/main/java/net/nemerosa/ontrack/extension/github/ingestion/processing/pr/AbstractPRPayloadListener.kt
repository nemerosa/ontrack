package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Branch

abstract class AbstractPRPayloadListener(
    val action: PRPayloadAction,
) : PRPayloadListener {

    override fun preProcessCheck(payload: PRPayload): PRPayloadListenerCheck =
        when {
            !sameRepo(payload.pullRequest.head, payload.pullRequest.base) -> PRPayloadListenerCheck.IGNORED
            payload.action == action -> PRPayloadListenerCheck.TO_BE_PROCESSED
            else -> PRPayloadListenerCheck.IGNORED
        }

    private fun sameRepo(head: Branch, base: Branch): Boolean =
        head.repo.owner.login == base.repo.owner.login &&
                head.repo.name == base.repo.name
}