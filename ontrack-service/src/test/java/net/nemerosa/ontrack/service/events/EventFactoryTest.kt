package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventFactoryImpl
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ValidationRunFixtures
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventFactoryTest {

    private val eventFactory: EventFactory = EventFactoryImpl()

    @Test
    fun `Available context for a validation run`() {
        val run = ValidationRunFixtures.testValidationRun()
        val event = eventFactory.newValidationRun(run)

        assertEquals(
            mapOf(
                ProjectEntityType.PROJECT to run.project,
                ProjectEntityType.BRANCH to run.build.branch,
                ProjectEntityType.BUILD to run.build,
                ProjectEntityType.VALIDATION_STAMP to run.validationStamp,
                ProjectEntityType.VALIDATION_RUN to run,
            ),
            event.entities
        )

        assertEquals(
            mapOf(
                "STATUS" to NameValue(
                    run.lastStatus.statusID.name,
                    run.lastStatus.statusID.id,
                )
            ),
            event.values
        )
    }

}