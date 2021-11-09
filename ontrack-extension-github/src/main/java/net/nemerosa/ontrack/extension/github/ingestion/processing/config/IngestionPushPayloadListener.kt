package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerOutcome
import org.springframework.stereotype.Component

@Component
class IngestionPushPayloadListener(
    private val configService: ConfigService,
) : PushPayloadListener {
    override fun process(payload: PushPayload): PushPayloadListenerOutcome {
        return if (payload.isAddedOrModified(INGESTION_CONFIG_FILE_PATH)) {
            configService.saveConfig(
                repository = payload.repository,
                branch = payload.branchName,
                path = INGESTION_CONFIG_FILE_PATH,
            )
            PushPayloadListenerOutcome.PROCESSED
        } else if (payload.isRemoved(INGESTION_CONFIG_FILE_PATH)) {
            configService.removeConfig(
                repository = payload.repository,
                branch = payload.branchName,
            )
            PushPayloadListenerOutcome.PROCESSED
        } else {
            PushPayloadListenerOutcome.IGNORED
        }
    }
}