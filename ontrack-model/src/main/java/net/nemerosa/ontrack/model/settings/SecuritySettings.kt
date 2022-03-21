package net.nemerosa.ontrack.model.settings

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo

/**
 * General security settings.
 *
 * @property isGrantProjectViewToAll `true` when all authenticated users have a read-only access to all projects.
 * @property isGrantProjectParticipationToAll `true` when all authenticated users have a participant access to all projects.
 */
class SecuritySettings(
    @get:JsonProperty("grantProjectViewToAll")
    val isGrantProjectViewToAll: Boolean,
    @get:JsonProperty("grantProjectParticipationToAll")
    val isGrantProjectParticipationToAll: Boolean,
    val builtInAuthenticationEnabled: Boolean = DEFAULT_BUILTIN_AUTHENTICATION_ENABLED,
) {

    companion object {
        const val DEFAULT_BUILTIN_AUTHENTICATION_ENABLED = true
    }

    fun form(): Form =
        Form.create()
            .with(
                YesNo.of("grantProjectViewToAll")
                    .label("Grants project view to all")
                    .help("Allows all authenticated users to have read-only access to all project.")
                    .value(isGrantProjectViewToAll)
            )
            .with(
                YesNo.of("grantProjectParticipationToAll")
                    .visibleIf("grantProjectViewToAll")
                    .label("Grants project participation to all")
                    .help("Allows all authenticated users to have participation access to all projects. They can add comments to the validation runs.")
                    .value(isGrantProjectParticipationToAll)
            )
            .with(
                YesNo.of(SecuritySettings::builtInAuthenticationEnabled.name)
                    .label("Built-in authentication")
                    .help("Enabling the built-in authentication")
                    .value(builtInAuthenticationEnabled)
            )
}
