package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.extension.recordings.store.RecordingsStore
import net.nemerosa.ontrack.extension.recordings.store.StoredRecording

abstract class AbstractRecordingsService(
        private val recordingsStore: RecordingsStore,
) : RecordingsService {

    override fun <R : Recording> record(extension: RecordingsExtension<R>, recording: R) {
        // TODO Gets the storage representation for the recordings
        val storedRecording = StoredRecording(
                id = recording.id,
                startTime = recording.startTime,
                endTime = recording.endTime,
                data = extension.toJson(recording),
        )
        // Saving the recordings
        recordingsStore.save(
                store = extension.id,
                recording = storedRecording,
        )
    }
}