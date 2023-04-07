package net.nemerosa.ontrack.extension.recordings.store

import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

@Component
class RecordingsStoreImpl(
        private val storageService: StorageService,
) : RecordingsStore {

    override fun save(store: String, recording: StoredRecording) {
        storageService.store(store, recording.id, recording)
    }

}