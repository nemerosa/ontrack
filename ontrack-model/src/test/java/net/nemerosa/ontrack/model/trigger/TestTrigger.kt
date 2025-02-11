package net.nemerosa.ontrack.model.trigger

import org.springframework.stereotype.Component

@Component
class TestTrigger : Trigger<TestTriggerData> {
    override val id: String = "test"
}