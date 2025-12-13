package net.nemerosa.ontrack.service.message

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.message.GlobalMessage
import net.nemerosa.ontrack.model.message.GlobalMessageService
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.model.message.MessageType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GlobalMessageServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var globalMessageService: GlobalMessageService

    @Autowired
    private lateinit var testGlobalMessageExtension: TestGlobalMessageExtension

    @Test
    fun `No message provided`() {
        testGlobalMessageExtension.messages.clear()
        val messages = globalMessageService.globalMessages
        assertTrue(messages.isEmpty(), "No message")
    }

    @Test
    fun `One message provided`() {
        testGlobalMessageExtension.messages.clear()
        testGlobalMessageExtension.messages += Message(
            "Some message",
            MessageType.ERROR
        )
        val messages = globalMessageService.globalMessages.filter { it.featureId == "test" }
        assertEquals(
            listOf(
                GlobalMessage(
                    "test",
                    "Some message",
                    MessageType.ERROR
                )
            ),
            messages
        )
    }

    @Test
    fun `Messages ordered by type`() {
        testGlobalMessageExtension.messages.clear()
        testGlobalMessageExtension.messages += Message(
            "License is about expire.",
            MessageType.WARNING
        )
        testGlobalMessageExtension.messages += Message(
            "License has expired.",
            MessageType.ERROR
        )
        val messages = globalMessageService.globalMessages.filter { it.featureId == "test" }
        assertEquals(
            listOf(
                GlobalMessage(
                    "test",
                    "License has expired.",
                    MessageType.ERROR
                ),
                GlobalMessage(
                    "test",
                    "License is about expire.",
                    MessageType.WARNING
                ),
            ),
            messages
        )
    }

}