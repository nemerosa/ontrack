package net.nemerosa.ontrack.extension.oidc

import org.springframework.security.oauth2.client.registration.ClientRegistration

/**
 * This interface wraps a [ClientRegistration], hiding
 * the details of this class to the code. This allows
 * also an easier testing process.
 */
interface OntrackClientRegistration {
    /**
     * Registration ID
     */
    val registrationId: String

    /**
     * Display name
     */
    val clientName: String
}

internal class WrappedOntrackClientRegistration(
        private val clientRegistration: ClientRegistration
) : OntrackClientRegistration {
    override val registrationId: String = clientRegistration.registrationId
    override val clientName: String = clientRegistration.clientName
}