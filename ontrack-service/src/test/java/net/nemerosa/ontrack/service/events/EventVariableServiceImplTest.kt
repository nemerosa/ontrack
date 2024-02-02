package net.nemerosa.ontrack.service.events

import io.mockk.mockk
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactoryImpl
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.events.SimpleEventType
import net.nemerosa.ontrack.model.structure.BranchFixtures
import net.nemerosa.ontrack.model.structure.ValidationRunFixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventVariableServiceImplTest {

    private val eventVariableService: EventVariableService = EventVariableServiceImpl(
        extensionManager = mockk(),
    )

    @Test
    fun `Context associated with a validation run`() {
        val run = ValidationRunFixtures.testValidationRun()
        val event = EventFactoryImpl().newValidationRun(run)
        // Gets the templating context
        val context = eventVariableService.getTemplateContext(event)

        // Checks all parameters
        assertEquals(
            mapOf(
                "project" to run.project,
                "branch" to run.build.branch,
                "build" to run.build,
                "validationStamp" to run.validationStamp,
                "validationRun" to run,
                "STATUS" to run.lastStatus.statusID.id,
                "STATUS_NAME" to run.lastStatus.statusID.name,
            ),
            context
        )
    }

    @Test
    fun `Entity context for the ref entity of an event`() {
        val branch = BranchFixtures.testBranch()
        val eventType = SimpleEventType.of("sample", "Not used in this test")
        val event = Event.of(eventType)
            .withRef(branch)
            .build()
        val context = eventVariableService.getTemplateContext(event)

        // Checks all parameters
        assertEquals(
            mapOf(
                "project" to branch.project,
                "branch" to branch,
                "entity" to branch,
            ),
            context
        )
    }

    @Test
    fun `Context for an extra entity`() {
        val branch = BranchFixtures.testBranch()
        val eventType = SimpleEventType.of("sample", "Not used in this test")
        val event = Event.of(eventType)
            .withExtra(branch)
            .build()
        val context = eventVariableService.getTemplateContext(event)

        // Checks all parameters
        assertEquals(
            mapOf(
                "x_project" to branch.project,
                "x_branch" to branch,
            ),
            context
        )
    }
}
