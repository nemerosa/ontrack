package net.nemerosa.ontrack.extension.recordings

interface RecordingsCleanupService {

    fun <R : Recording> cleanup(extension: RecordingsExtension<R>)

}