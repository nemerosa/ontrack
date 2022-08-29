package net.nemerosa.ontrack.extension.license.message

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.api.GlobalMessageExtension
import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.extension.license.LicenseConfigurationProperties
import net.nemerosa.ontrack.extension.license.LicenseExtensionFeature
import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.message.Message
import net.nemerosa.ontrack.model.message.MessageType
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class LicenseMessage(
    extensionFeature: LicenseExtensionFeature,
    private val licenseService: LicenseService,
    private val licenseConfigurationProperties: LicenseConfigurationProperties,
    private val structureService: StructureService,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), GlobalMessageExtension {

    override val globalMessages: List<Message>
        get() = checkLicense(licenseService.license)

    internal fun checkLicense(license: License?): List<Message> {
        val messages = mutableListOf<Message>()
        if (license != null) {
            if (!license.active) {
                messages += Message(
                    type = MessageType.ERROR,
                    content = """License "${license.name}" is not active."""
                )
            }
            when {
                isExpired(license) -> messages += Message(
                    type = MessageType.ERROR,
                    content = """License "${license.name}" is expired. It was valid until ${license.validUntil}."""
                )
                isAlmostExpired(license) -> messages += Message(
                    type = MessageType.WARNING,
                    content = """License "${license.name}" expires at ${license.validUntil}."""
                )
            }
            if (license.maxProjects > 0) {
                val count = securityService.asAdmin {
                    structureService.projectList.size
                }
                if (count >= license.maxProjects) {
                    messages += Message(
                        type = MessageType.ERROR,
                        content = """Maximum number of projects (${license.maxProjects}) for license "${license.name}" has been exceeded. No new project can be created."""
                    )
                }
            }
        }
        return messages
    }

    internal fun isAlmostExpired(license: License) =
        if (license.validUntil != null) {
            val now = Time.now()
            val validUntil = license.validUntil
            val warningTime = validUntil.minus(licenseConfigurationProperties.warning)
            now >= warningTime && now < validUntil
        } else {
            false
        }

    internal fun isExpired(license: License) =
        if (license.validUntil != null) {
            val now = Time.now()
            val validUntil = license.validUntil
            now >= validUntil
        } else {
            false
        }
}
