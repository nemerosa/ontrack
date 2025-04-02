package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Component

@Component
class MockEventTypeFactory(
    private val eventFactory: EventFactory,
) : StartupService {

    override fun getName(): String = "Registration of mock event"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        eventFactory.register(MockEventType)
    }
}