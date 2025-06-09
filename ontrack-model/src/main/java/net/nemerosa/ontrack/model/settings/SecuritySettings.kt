package net.nemerosa.ontrack.model.settings

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * General security settings.
 *
 * @property isGrantProjectViewToAll `true` when all authenticated users have a read-only access to all projects.
 * @property isGrantProjectParticipationToAll `true` when all authenticated users have a participant access to all projects.
 */
@APIDescription("General security settings")
class SecuritySettings(
    @get:JsonProperty("grantProjectViewToAll")
    @APIDescription("Grants project view to all")
    val isGrantProjectViewToAll: Boolean = true,
    @get:JsonProperty("grantProjectParticipationToAll")
    @APIDescription("Grants project participation to all")
    val isGrantProjectParticipationToAll: Boolean = true,
    @APIDescription("Enabling the built-in authentication")
    val builtInAuthenticationEnabled: Boolean = DEFAULT_BUILTIN_AUTHENTICATION_ENABLED,
    @APIDescription("Grants dashboard creation rights to all")
    val grantDashboardEditionToAll: Boolean = DEFAULT_GRANT_DASHBOARD_EDITION,
    @APIDescription("Grants dashboard sharing rights to all")
    val grantDashboardSharingToAll: Boolean = DEFAULT_GRANT_DASHBOARD_SHARING,
) {

    companion object {
        const val DEFAULT_BUILTIN_AUTHENTICATION_ENABLED = true
        const val DEFAULT_GRANT_DASHBOARD_EDITION = true
        const val DEFAULT_GRANT_DASHBOARD_SHARING = true
    }

}
