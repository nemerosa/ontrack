package net.nemerosa.ontrack.kdsl.spec.settings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SecuritySettings(
    val grantProjectViewToAll: Boolean,
    val grantProjectParticipationToAll: Boolean,
    val builtInAuthenticationEnabled: Boolean,
) {

    fun withGrantProjectViewToAll(grantProjectViewToAll: Boolean) = SecuritySettings(
        grantProjectViewToAll = grantProjectViewToAll,
        grantProjectParticipationToAll = grantProjectParticipationToAll,
        builtInAuthenticationEnabled = builtInAuthenticationEnabled,
    )

}
