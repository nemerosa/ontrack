package net.nemerosa.ontrack.extension.general.message

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.message.GlobalMessage
import net.nemerosa.ontrack.model.message.GlobalMessageService
import net.nemerosa.ontrack.model.message.MessageType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SystemMessageProviderIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var globalMessageService: GlobalMessageService

    @Test
    fun `No system message by default`() {
        asAdmin {
            withCleanSettings<SystemMessageSettings> {
                val messages = globalMessageService.globalMessages
                assertTrue(
                    messages.none { it.featureId == "general" },
                    "No global message: $messages"
                )
            }
        }
    }

    @Test
    fun `Setting a system message`() {
        asAdmin {
            withCleanSettings<SystemMessageSettings> {
                settingsManagerService.saveSettings(
                    SystemMessageSettings(
                        content = "License has expired.",
                        type = MessageType.ERROR
                    )
                )
                val messages = globalMessageService.globalMessages
                assertEquals(
                    listOf(
                        GlobalMessage(
                            featureId = "general",
                            content = "License has expired.",
                            type = MessageType.ERROR
                        )
                    ),
                    messages.filter { it.featureId == "general" }
                )
            }
        }
    }

}