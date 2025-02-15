package net.nemerosa.ontrack.model.trigger

import org.springframework.stereotype.Component

@Component
class TestTrigger : Trigger<TestTriggerData> {
    override val id: String = "test"
    override val displayName: String = "Test"

    override fun filterCriteria(token: String, criterias: MutableList<String>, params: MutableMap<String, Any?>) {
        criterias += "TRIGGER_DATA::JSONB->>'message' ilike :message"
        params["message"] = "%$token%"
    }
}