package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.extension.queue.QueueExtensionFeature
import net.nemerosa.ontrack.extension.recordings.RecordingsExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class QueueRecordingsExtension(
        queueExtensionFeature: QueueExtensionFeature
) : AbstractExtension(queueExtensionFeature), RecordingsExtension<QueueRecord> {

    override val id: String = "queue-recordings"
    
}