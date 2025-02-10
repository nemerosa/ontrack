package net.nemerosa.ontrack.extension.environments.trigger

import net.nemerosa.ontrack.model.trigger.Trigger
import org.springframework.stereotype.Component

@Component
class SlotPipelineTrigger : Trigger<SlotPipelineTriggerData> {
    override val id: String = "slot-pipeline"
}