package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.extension.recordings.store.RecordingsStore
import net.nemerosa.ontrack.extension.recordings.store.StoredRecording

abstract class AbstractRecordingsService(
        private val recordingsStore: RecordingsStore,
) : RecordingsService {

    override fun <R : Recording> record(extension: RecordingsExtension<R>, recording: R) {
        // Gets the storage representation for the recordings
        val storedRecording = toStoredRecording(extension, recording)
        // Saving the recordings
        recordingsStore.save(
                store = extension.id,
                recording = storedRecording,
        )
    }

    private fun <R : Recording> toStoredRecording(extension: RecordingsExtension<R>, recording: R) =
            StoredRecording(
                    id = recording.id,
                    startTime = recording.startTime,
                    endTime = recording.endTime,
                    data = extension.toJson(recording),
            )

    override fun <R : Recording> updateRecord(extension: RecordingsExtension<R>, id: String, updating: (R) -> R) {
        val storedRecording = recordingsStore.findById(extension.id, id)
                ?: throw RecordingNotFoundException(extension.id, id)
        val recording = extension.fromJson(storedRecording.data)
        val newRecording = updating(recording)
        record(extension, newRecording)
    }
}