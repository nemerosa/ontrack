package net.nemerosa.ontrack.extension.oidc

import net.nemerosa.ontrack.extension.api.UILogin
import net.nemerosa.ontrack.extension.api.UILoginExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.stereotype.Component

@Component
class OIDCUILoginExtension(
        extensionFeature: OIDCExtensionFeature,
        private val clientRegistrationRepository: OntrackClientRegistrationRepository
) : AbstractExtension(extensionFeature), UILoginExtension {

    override val contributions: List<UILogin>
        get() = clientRegistrationRepository.map { registration ->
            UILogin(
                    link = "/oauth2/authorization/${registration.registrationId}",
                    name = registration.clientName
            )
        }
}