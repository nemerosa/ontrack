package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.extension.notifications.AbstractNotificationTestSupport
import net.nemerosa.ontrack.extension.notifications.mock.MockNotificationChannelConfig
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ValidationRunSubscriptionIT : AbstractNotificationTestSupport() {

    @Test
    fun `Subscribing to a failed validation run using the exact validation stamp name`() {
        asAdmin {
            project {
                branch {
                    val vsa = validationStamp()
                    val vsb = validationStamp()
                    // Subscribing at validation stamp level for the B validation
                    val target = uid("t_")
                    eventSubscriptionService.subscribe(
                        name = uid("p"),
                        channel = mockNotificationChannel,
                        channelConfig = MockNotificationChannelConfig(target),
                        projectEntity = vsb,
                        keywords = "failed", // Status
                        origin = "test",
                        contentTemplate = null,
                        EventFactory.NEW_VALIDATION_RUN
                    )
                    build {
                        // VSA passed validation --> no message
                        validate(vsa, validationRunStatusID = ValidationRunStatusID.STATUS_PASSED)
                        assertNull(mockNotificationChannel.messages[target], "No message")
                        // VSA failed validation --> no message
                        validate(vsa, validationRunStatusID = ValidationRunStatusID.STATUS_FAILED)
                        assertNull(mockNotificationChannel.messages[target], "No message")
                        // VSB passed validation --> no message
                        validate(vsb, validationRunStatusID = ValidationRunStatusID.STATUS_PASSED)
                        assertNull(mockNotificationChannel.messages[target], "No message")
                        // VSB failed validation --> we get a message
                        validate(vsb, validationRunStatusID = ValidationRunStatusID.STATUS_FAILED)
                        assertNotNull(mockNotificationChannel.messages[target], "Message received") { messages ->
                            assertEquals(1, messages.size, "One message received")
                            assertEquals(
                                "Build $name has run for the ${vsb.name} with status Failed in branch ${branch.name} in ${project.name}.",
                                messages.first()
                            )
                        }
                    }
                }
            }
        }
    }

}