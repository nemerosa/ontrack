package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.getOrCreateBranch
import org.springframework.stereotype.Component

/**
 * When a PR is `opened`, we just create the branch.
 */
@Component
class OpenedPRPayloadListener(
    private val ingestionModelAccessService: IngestionModelAccessService,
) : AbstractPRPayloadListener(
    action = PRPayloadAction.opened
) {
    override fun process(payload: PRPayload, configuration: String?) {
        ingestionModelAccessService.getOrCreateBranch(
            repository = payload.repository,
            configuration = configuration,
            headBranch = payload.pullRequest.head.ref,
            pullRequest = payload.pullRequest,
        )
    }
}