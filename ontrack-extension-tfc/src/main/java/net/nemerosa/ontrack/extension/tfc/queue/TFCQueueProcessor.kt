package net.nemerosa.ontrack.extension.tfc.queue

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.tfc.service.TFCPayload
import org.springframework.stereotype.Component

@Component
class TFCQueueProcessor: QueueProcessor<TFCPayload> {

}