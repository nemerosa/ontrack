package net.nemerosa.ontrack.extension.tfc.queue

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.tfc.service.RunPayload
import net.nemerosa.ontrack.extension.tfc.service.TFCService
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class TFCQueueProcessor(
    private val tfcService: TFCService,
) : QueueProcessor<RunPayload> {

    override val id: String = "tfc"

    override fun getRoutingIdentifier(payload: RunPayload): String = payload.workspaceName

    override val payloadType: KClass<RunPayload> = RunPayload::class

    override fun process(payload: RunPayload) {
        val status = if (payload.runStatus == "applied") {
            ValidationRunStatusID.STATUS_PASSED
        } else {
            ValidationRunStatusID.STATUS_DEFECTIVE
        }
        tfcService.validate(
            params = payload.parameters,
            status = status,
            workspaceId = payload.workspaceId,
            runUrl = payload.runUrl,
        )
        TODO("Not yet implemented")
    }

}