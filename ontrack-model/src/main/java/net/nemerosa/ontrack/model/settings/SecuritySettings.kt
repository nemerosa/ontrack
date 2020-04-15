package net.nemerosa.ontrack.model.settings

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo

/**
 * General security settings.
 *
 * @property grantProjectViewToAll `true` when all authenticated users have a read-only access to all projects.
 */
class SecuritySettings(
        @JsonProperty("grantProjectViewToAll")
        val isGrantProjectViewToAll: Boolean
) {
    fun form(): Form =
            Form.create()
                    .with(
                            YesNo.of("grantProjectViewToAll")
                                    .label("Grants project view to all")
                                    .help("Unless disabled at project level, this would enable any user (even anonymous) " +
                                            "to view the content of all projects.")
                                    .value(isGrantProjectViewToAll)
                    )
}
