package net.nemerosa.ontrack.extension.tfc.queue

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.tfc.service.TFCPayload
import net.nemerosa.ontrack.extension.tfc.service.TFCService
import org.springframework.stereotype.Component

@Component
class TFCQueueProcessor(
    private val tfcService: TFCService,
): QueueProcessor<TFCPayload> {

    override fun process(payload: TFCPayload) {
        TODO("Not yet implemented")
    }

}