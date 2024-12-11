package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.message.GlobalMessageService
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.model.message.MessageType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SystemMessageSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var globalMessageService: GlobalMessageService

    @Test
    fun `Casc for system message`() {
        asAdmin {
            withCleanSettings<SystemMessageSettings> {
                casc(
                    """
                        ontrack:
                            config:
                                settings:
                                    system-message:
                                        type: WARNING
                                        content: This is a global warning!
                    """.trimIndent()
                )
                val messages = globalMessageService.globalMessages
                assertEquals(
                    listOf(
                        Message(
                            content = "This is a global warning!",
                            type = MessageType.WARNING
                        )
                    ),
                    messages
                )
                casc(
                    """
                        ontrack:
                            config:
                                settings:
                                    system-message:
                                        type: INFO
                                        content: ""
                    """.trimIndent()
                )
                assertTrue(
                    globalMessageService.globalMessages.isEmpty(),
                    "No more messages"
                )
            }
        }
    }

}