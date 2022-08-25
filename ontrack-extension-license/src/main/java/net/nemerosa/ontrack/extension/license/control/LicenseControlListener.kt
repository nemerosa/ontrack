package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class LicenseControlListener(
    private val licenseService: LicenseService,
    private val structureService: StructureService,
    private val securityService: SecurityService,
): EventListener {

    override fun onEvent(event: Event) {
        if (event.eventType == EventFactory.NEW_PROJECT) {
            val license = licenseService.license
            if (license != null) {
                val count = securityService.asAdmin {
                    structureService.projectList.size
                }
                LicenseControl.control(license, count)
            }
        }
    }

}