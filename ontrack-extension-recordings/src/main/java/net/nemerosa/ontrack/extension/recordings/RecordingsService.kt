package net.nemerosa.ontrack.extension.recordings

interface RecordingsService {

    fun <R : Recording, F : Any> record(
            extension: RecordingsExtension<R, F>,
            recording: R,
    )

    fun <R : Recording, F : Any> updateRecord(
            extension: RecordingsExtension<R, F>,
            id: String,
            updating: (R) -> R,
    )

}