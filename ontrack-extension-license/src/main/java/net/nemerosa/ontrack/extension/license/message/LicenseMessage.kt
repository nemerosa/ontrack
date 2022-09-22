package net.nemerosa.ontrack.extension.license.message

import net.nemerosa.ontrack.extension.api.GlobalMessageExtension
import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseExtensionFeature
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.extension.license.control.LicenseExpiration
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.model.message.MessageType
import org.springframework.stereotype.Component

@Component
class LicenseMessage(
    extensionFeature: LicenseExtensionFeature,
    private val licenseService: LicenseService,
    private val licenseControlService: LicenseControlService,
) : AbstractExtension(extensionFeature), GlobalMessageExtension {

    override val globalMessages: List<Message>
        get() = checkLicense(licenseService.license)

    internal fun checkLicense(license: License?): List<Message> {
        val messages = mutableListOf<Message>()
        if (license != null) {
            val control = licenseControlService.control(license)
            if (!control.active) {
                messages += Message(
                    type = MessageType.ERROR,
                    content = """License "${license.name}" is not active."""
                )
            }
            when (control.expiration) {
                LicenseExpiration.EXPIRED -> messages += Message(
                    type = MessageType.ERROR,
                    content = """License "${license.name}" is expired. It was valid until ${license.validUntil}."""
                )
                LicenseExpiration.ALMOST -> messages += Message(
                    type = MessageType.WARNING,
                    content = """License "${license.name}" expires at ${license.validUntil}."""
                )
                else -> {} // Nothing
            }
            if (control.projectCountExceeded) {
                messages += Message(
                    type = MessageType.ERROR,
                    content = """Maximum number of projects (${license.maxProjects}) for license "${license.name}" has been exceeded. No new project can be created."""
                )
            }
        }
        return messages
    }
}
