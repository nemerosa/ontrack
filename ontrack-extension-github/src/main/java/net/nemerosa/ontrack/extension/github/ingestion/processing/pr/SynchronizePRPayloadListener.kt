package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.old.INGESTION_CONFIG_FILE_PATH
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.getOrCreateBranch
import org.springframework.stereotype.Component

/**
 * When a PR is `synchronize`, we update the ingestion configuration for the branch.
 */
@Component
class SynchronizePRPayloadListener(
    private val ingestionModelAccessService: IngestionModelAccessService,
    private val configService: ConfigService,
) : AbstractPRPayloadListener(
    action = PRPayloadAction.synchronize
) {
    override fun process(payload: PRPayload, configuration: String?): IngestionEventProcessingResultDetails {
        // Gets or creates the branch
        val branch = ingestionModelAccessService.getOrCreateBranch(
            repository = payload.repository,
            configuration = configuration,
            headBranch = payload.pullRequest.head.ref,
            pullRequest = payload.pullRequest,
        )
        // Loads and saves the ingestion configuration
        configService.loadAndSaveConfig(branch, INGESTION_CONFIG_FILE_PATH)
        // OK
        return IngestionEventProcessingResultDetails.processed()
    }
}