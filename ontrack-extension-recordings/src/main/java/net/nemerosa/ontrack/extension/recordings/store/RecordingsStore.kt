package net.nemerosa.ontrack.extension.recordings.store

import java.time.LocalDateTime

interface RecordingsStore {

    fun save(store: String, recording: StoredRecording)

    fun removeAllBefore(store: String, retentionDate: LocalDateTime, nonRunningOnly: Boolean)

}