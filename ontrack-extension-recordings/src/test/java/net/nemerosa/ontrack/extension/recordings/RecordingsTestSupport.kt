package net.nemerosa.ontrack.extension.recordings

import org.springframework.stereotype.Component

@Component
class RecordingsTestSupport(
        private val recordingsService: RecordingsService,
) {

    fun <R : Recording, F : Any> record(extension: RecordingsExtension<R, F>, recording: R) {
        recordingsService.record(extension, recording)
    }
}