package net.nemerosa.ontrack.extensions.environments.security

import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.environmentFeatureEnabled
import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class SlotsAuthorizationContributor(
    private val licenseControlService: LicenseControlService,
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        private const val SLOT = "slot"
    }

    override fun appliesTo(context: Any): Boolean = context is Slot

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> {
        val slot = context as Slot
        val environmentFeatureEnabled = licenseControlService.environmentFeatureEnabled
        return listOf(
            Authorization(
                name = SLOT,
                action = Authorization.VIEW,
                authorized = environmentFeatureEnabled &&
                        securityService.isProjectFunctionGranted(slot.project, SlotView::class.java)
            ),
            Authorization(
                name = SLOT,
                action = Authorization.DELETE,
                authorized = environmentFeatureEnabled &&
                        securityService.isProjectFunctionGranted(slot.project, SlotDelete::class.java)
            ),
            Authorization(
                name = SLOT,
                action = Authorization.EDIT,
                authorized = environmentFeatureEnabled &&
                        securityService.isProjectFunctionGranted(slot.project, SlotUpdate::class.java)
            ),
            Authorization(
                name = "pipeline",
                action = "create",
                authorized = environmentFeatureEnabled &&
                        securityService.isProjectFunctionGranted(slot.project, SlotPipelineCreate::class.java)
            ),
        )
    }

}