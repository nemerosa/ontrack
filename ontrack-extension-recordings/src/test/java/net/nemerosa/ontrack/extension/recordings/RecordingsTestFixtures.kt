package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.recordings.store.StoredRecording
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid

object RecordingsTestFixtures {

    fun sampleRecord(
            id: String = uid("test_"),
            message: String = uid("msg_"),
    ) = StoredRecording(
            id = id,
            startTime = Time.now(),
            endTime = null,
            data = mapOf("message" to message).asJson(),
    )

    const val testStore = "test"

}