package net.nemerosa.ontrack.extension.license.message

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.license.AbstractLicenseTestSupport
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.model.message.MessageType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

internal class LicenseMessageIT : AbstractLicenseTestSupport() {

    @Autowired
    private lateinit var licenseMessage: LicenseMessage

    @Test
    fun `When no license, no message`() {
        withLicense {
            val messages = licenseMessage.globalMessages
            assertEquals(
                emptyList(),
                messages,
                "No message"
            )
        }
    }

    @Test
    fun `Warning when about to expire`() {
        val validUntil = Time.now().plusDays(10)
        withLicense(validUntil = validUntil) {
            val messages = licenseMessage.globalMessages
            assertEquals(
                listOf(
                    Message(
                        type = MessageType.WARNING,
                        content = """License "Test license" expires at $validUntil."""
                    )
                ),
                messages
            )
        }
    }

    @Test
    fun `Error when about expired`() {
        val validUntil = Time.now().minusDays(1)
        withLicense(validUntil = validUntil) {
            val messages = licenseMessage.globalMessages
            assertEquals(
                listOf(
                    Message(
                        type = MessageType.ERROR,
                        content = """License "Test license" is expired. It was valid until $validUntil."""
                    )
                ),
                messages
            )
        }
    }

    @Test
    fun `Error when project count exceeded`() {
        repeat(2) {
            project()
        }
        asAdmin {
            val count = structureService.projectList.size
            withLicense(maxProjects = count) {
                val messages = licenseMessage.globalMessages
                assertEquals(
                    listOf(
                        Message(
                            type = MessageType.ERROR,
                            content = """Maximum number of projects ($count) for license "Test license" has been exceeded. No new project can be created."""
                        )
                    ),
                    messages
                )
            }
        }
    }

    @Test
    fun `Error and warning when project count exceeded and almost expired`() {
        repeat(2) {
            project()
        }
        asAdmin {
            val validUntil = Time.now().plusDays(10)
            val count = structureService.projectList.size
            withLicense(maxProjects = count, validUntil = validUntil) {
                val messages = licenseMessage.globalMessages
                assertEquals(
                    listOf(
                        Message(
                            type = MessageType.ERROR,
                            content = """Maximum number of projects ($count) for license "Test license" has been exceeded. No new project can be created."""
                        ),
                        Message(
                            type = MessageType.WARNING,
                            content = """License "Test license" expires at $validUntil."""
                        ),
                    ),
                    messages.sortedWith(compareBy(Message::type))
                )
            }
        }
    }

}