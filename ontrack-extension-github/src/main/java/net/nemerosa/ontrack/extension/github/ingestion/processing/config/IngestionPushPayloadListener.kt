package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerCheck
import org.springframework.stereotype.Component

@Component
class IngestionPushPayloadListener(
    private val configService: ConfigService,
) : PushPayloadListener {

    override fun preProcessCheck(payload: PushPayload): PushPayloadListenerCheck =
        if (payload.isAddedOrModified(INGESTION_CONFIG_FILE_PATH) || payload.isRemoved(INGESTION_CONFIG_FILE_PATH)) {
            PushPayloadListenerCheck.TO_BE_PROCESSED
        } else {
            PushPayloadListenerCheck.IGNORED
        }

    override fun process(payload: PushPayload) {
        when {
            payload.isAddedOrModified(INGESTION_CONFIG_FILE_PATH) -> configService.saveConfig(
                repository = payload.repository,
                branch = payload.branchName,
                path = INGESTION_CONFIG_FILE_PATH,
            )
            payload.isRemoved(INGESTION_CONFIG_FILE_PATH) -> configService.removeConfig(
                repository = payload.repository,
                branch = payload.branchName,
            )
        }
    }
}