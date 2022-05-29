package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.getOrCreateBranch
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * When a PR is `closed`, disables the branch.
 */
@Component
class ClosedPRPayloadListener(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val structureService: StructureService,
) : AbstractPRPayloadListener(
    action = PRPayloadAction.closed
) {
    override fun process(payload: PRPayload, configuration: String?): IngestionEventProcessingResultDetails {
        val branch = ingestionModelAccessService.getOrCreateBranch(
            repository = payload.repository,
            configuration = configuration,
            headBranch = payload.pullRequest.head.ref,
            pullRequest = payload.pullRequest,
        )
        structureService.disableBranch(branch)
        return IngestionEventProcessingResultDetails.processed()
    }
}