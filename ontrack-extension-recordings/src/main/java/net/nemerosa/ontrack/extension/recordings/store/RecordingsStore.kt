package net.nemerosa.ontrack.extension.recordings.store

interface RecordingsStore {

    fun save(store: String, recording: StoredRecording)

}