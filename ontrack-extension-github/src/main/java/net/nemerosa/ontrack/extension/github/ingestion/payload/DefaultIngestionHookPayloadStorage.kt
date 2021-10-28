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

    override fun count(): Int {
        return storageService.count(store = INGESTION_HOOK_PAYLOAD_STORE)
    }

    override fun list(offset: Int, size: Int): List<IngestionHookPayload> {
        return storageService.filter(
            store = INGESTION_HOOK_PAYLOAD_STORE,
            type = IngestionHookPayload::class,
            offset = offset,
            size = size
            // TODO Order by timestamp desc
        )
    }

    companion object {
        private const val INGESTION_HOOK_PAYLOAD_STORE = "github.IngestionHookPayload"
    }

}