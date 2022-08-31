package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.LicenseService
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import org.springframework.stereotype.Component

@Component
class LicenseControlListener(
    private val licenseService: LicenseService,
    private val licenseControlService: LicenseControlService,
) : EventListener {

    override fun onEvent(event: Event) {
        if (event.eventType == EventFactory.NEW_PROJECT) {
            val license = licenseService.license
            val control = licenseControlService.control(license)
            if (license != null && !control.valid) {
                if (!control.active) {
                    throw LicenseInactiveException(license)
                }
                if (control.expiration == LicenseExpiration.EXPIRED) {
                    throw LicenseExpiredException(license)
                }
                if (control.projectCountExceeded) {
                    throw LicenseMaxProjectExceededException(license)
                }
            }
        }
    }

}