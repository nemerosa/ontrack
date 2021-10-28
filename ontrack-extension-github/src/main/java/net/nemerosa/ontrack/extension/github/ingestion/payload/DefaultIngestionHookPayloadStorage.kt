package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class DefaultIngestionHookPayloadStorage(
    private val storageService: StorageService,
) : IngestionHookPayloadStorage {

    override fun store(payload: IngestionHookPayload) {
        storageService.store(
            INGESTION_HOOK_PAYLOAD_STORE,
            payload.uuid.toString(),
            payload,
        )
    }

    companion object {
        private const val INGESTION_HOOK_PAYLOAD_STORE = "github.IngestionHookPayload"
    }

}