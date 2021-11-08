package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.config.ConfigService
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListener
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.PushPayloadListenerOutcome
import org.springframework.stereotype.Component

@Component
class IngestionPushPayloadListener(
    private val configService: ConfigService,
) : PushPayloadListener {
    override fun process(payload: PushPayload): PushPayloadListenerOutcome {
        return if (payload.isAddedOrModified(CONFIG_FILE_PATH)) {
            configService.saveConfig(
                owner = payload.repository.owner.login,
                repository = payload.repository.name,
                branch = payload.branchName,
                path = CONFIG_FILE_PATH,
            )
            PushPayloadListenerOutcome.PROCESSED
        } else if (payload.isRemoved(CONFIG_FILE_PATH)) {
            configService.removeConfig(
                owner = payload.repository.owner.login,
                repository = payload.repository.name,
                branch = payload.branchName,
            )
            PushPayloadListenerOutcome.PROCESSED
        } else {
            PushPayloadListenerOutcome.IGNORED
        }
    }

    companion object {
        const val CONFIG_FILE_PATH = ".github/ontrack/ingestion.yaml"
    }
}