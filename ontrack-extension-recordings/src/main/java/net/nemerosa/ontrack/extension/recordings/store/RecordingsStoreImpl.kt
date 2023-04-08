package net.nemerosa.ontrack.extension.recordings.store

import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RecordingsStoreImpl(
        private val storageService: StorageService,
) : RecordingsStore {

    override fun save(store: String, recording: StoredRecording) {
        storageService.store(store, recording.id, recording)
    }

    override fun findById(store: String, id: String): StoredRecording? =
            storageService.find(store, id, StoredRecording::class)

    override fun removeAllBefore(store: String, retentionDate: LocalDateTime, nonRunningOnly: Boolean) {
        if (nonRunningOnly) {
            storageService.deleteWithFilter(
                    store = store,
                    query = "data::jsonb->>'endTime' IS NOT NULL AND data::jsonb->>'startTime' <= :beforeTime",
                    queryVariables = mapOf(
                            "beforeTime" to AbstractJdbcRepository.dateTimeForDB(retentionDate)
                    )
            )
        } else {
            storageService.deleteWithFilter(
                    store = store,
                    query = "data::jsonb->>'startTime' <= :beforeTime",
                    queryVariables = mapOf(
                            "beforeTime" to AbstractJdbcRepository.dateTimeForDB(retentionDate)
                    )
            )
        }
    }
}