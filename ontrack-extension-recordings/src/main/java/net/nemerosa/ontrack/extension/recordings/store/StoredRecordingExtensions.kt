package net.nemerosa.ontrack.extension.recordings.store

import net.nemerosa.ontrack.extension.recordings.Recording
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension

fun <R : Recording, F : Any> R.toStoredRecording(extension: RecordingsExtension<R, F>) =
        StoredRecording(
                id = id,
                startTime = startTime,
                endTime = endTime,
                data = extension.toJson(this),
        )


fun <R : Recording, F : Any> StoredRecording.toRecording(extension: RecordingsExtension<R, F>) =
        extension.fromJson(data)
