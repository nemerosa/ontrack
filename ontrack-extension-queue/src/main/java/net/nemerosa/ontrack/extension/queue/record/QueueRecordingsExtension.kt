package net.nemerosa.ontrack.extension.queue.record

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

@Component
class QueueRecordingsExtension(
        queueExtensionFeature: QueueExtensionFeature
) : AbstractExtension(queueExtensionFeature), RecordingsExtension<QueueRecord> {

    override val id: String = "queue-recordings"

    override val displayName: String = "Queue messages"

    override fun toJson(recording: QueueRecord): JsonNode = recording.asJson()

}