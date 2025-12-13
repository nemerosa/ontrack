package net.nemerosa.ontrack.extension.license.message

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.license.LicenseExtensionFeature
import net.nemerosa.ontrack.extension.license.LicenseFixtures.testLicense
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.extension.license.control.LicenseControl
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.model.message.MessageType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LicenseMessageTest() {

    private lateinit var licenseService: LicenseService
    private lateinit var licenseControlService: LicenseControlService
    private lateinit var licenseMessage: LicenseMessage

    @BeforeEach
    fun before() {
        licenseService = mockk()
        licenseControlService = mockk()
        licenseMessage = LicenseMessage(
            extensionFeature = LicenseExtensionFeature(),
            licenseService = licenseService,
            licenseControlService = licenseControlService,
        )
    }

    @Test
    fun `Valid license with no message does not produce any message`() {
        val license = testLicense()
        every { licenseService.license } returns license
        every { licenseControlService.control(license) } returns LicenseControl.OK
        val messages = licenseMessage.globalMessages
        assertTrue(messages.isEmpty(), "No message")
    }

    @Test
    fun `Valid license with message produces a message`() {
        val license = testLicense(message = "Sample message")
        every { licenseService.license } returns license
        every { licenseControlService.control(license) } returns LicenseControl.OK
        val messages = licenseMessage.globalMessages
        assertNotNull(messages.firstOrNull(), "Message is present") { message ->
            assertEquals(MessageType.WARNING, message.type)
            assertEquals("Sample message", message.content)
        }
    }

}