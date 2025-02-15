package net.nemerosa.ontrack.extension.environments.trigger

import net.nemerosa.ontrack.model.trigger.Trigger
import org.springframework.stereotype.Component

@Component
class SlotPipelineTrigger : Trigger<SlotPipelineTriggerData> {
    override val id: String = "slot-pipeline"
    override val displayName: String = "Deployment"

    override fun filterCriteria(token: String, criterias: MutableList<String>, params: MutableMap<String, Any?>) {
        criterias += "TRIGGER_DATA::JSONB->>'pipelineId' = :pipelineId"
        params["pipelineId"] = token
    }
}