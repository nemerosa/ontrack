package net.nemerosa.ontrack.extension.recordings

interface RecordingsCleanupService {

    fun <R : Recording, F : Any> cleanup(extension: RecordingsExtension<R, F>)

}