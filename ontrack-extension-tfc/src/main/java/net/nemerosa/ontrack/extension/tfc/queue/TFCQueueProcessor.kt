package net.nemerosa.ontrack.extension.tfc.queue

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.tfc.service.RunPayload
import net.nemerosa.ontrack.extension.tfc.service.TFCService
import org.springframework.stereotype.Component

@Component
class TFCQueueProcessor(
    private val tfcService: TFCService,
) : QueueProcessor<RunPayload> {

    override val id: String = "tfc"

    override fun getRoutingIdentifier(payload: RunPayload): String = payload.workspaceName

    override fun process(payload: RunPayload) {
        TODO("Not yet implemented")
    }

}