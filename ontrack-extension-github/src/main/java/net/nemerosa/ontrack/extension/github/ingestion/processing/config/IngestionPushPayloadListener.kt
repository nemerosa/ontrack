package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.config.parser.old.INGESTION_CONFIG_FILE_PATH
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerCheck
import net.nemerosa.ontrack.extension.github.ingestion.support.IngestionModelAccessService
import net.nemerosa.ontrack.extension.github.ingestion.support.getOrCreateBranch
import org.springframework.stereotype.Component

@Component
class IngestionPushPayloadListener(
    private val configService: ConfigService,
    private val ingestionModelAccessService: IngestionModelAccessService,
) : PushPayloadListener {

    override fun preProcessCheck(payload: PushPayload): PushPayloadListenerCheck =
        if (payload.getTag() != null) {
            PushPayloadListenerCheck.IGNORED
        } else if (payload.isAddedOrModified(INGESTION_CONFIG_FILE_PATH) || payload.isRemoved(INGESTION_CONFIG_FILE_PATH)) {
            PushPayloadListenerCheck.TO_BE_PROCESSED
        } else {
            PushPayloadListenerCheck.IGNORED
        }

    override fun process(payload: PushPayload, configuration: String?): IngestionEventProcessingResultDetails {
        val branch = ingestionModelAccessService.getOrCreateBranch(
            repository = payload.repository,
            configuration = configuration,
            headBranch = payload.branchName,
            pullRequest = null, // No PR on push (see PR `synchronize` event)
        )
        when {
            payload.isAddedOrModified(INGESTION_CONFIG_FILE_PATH) -> configService.loadAndSaveConfig(
                branch = branch,
                path = INGESTION_CONFIG_FILE_PATH,
            )
            payload.isRemoved(INGESTION_CONFIG_FILE_PATH) -> configService.removeConfig(
                branch = branch,
            )
        }
        return IngestionEventProcessingResultDetails.processed()
    }
}