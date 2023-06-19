package net.nemerosa.ontrack.extension.tfc.settings

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

data class TFCSettings(
    @APIDescription("Is the support for TFC notifications enabled?")
    @APILabel("Enabled")
    val enabled: Boolean = false,
    @APIDescription("Secret token to be passed by TFC")
    @APILabel("Token")
    val token: String = "",
)