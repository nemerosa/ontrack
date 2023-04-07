package net.nemerosa.ontrack.extension.recordings

interface RecordingsService {

    fun <R : Recording> record(
            extension: RecordingsExtension<R>,
            recording: R,
    )

    fun <R : Recording> updateRecord(
            extension: RecordingsExtension<R>,
            id: String,
            updating: (R) -> R,
    )

}