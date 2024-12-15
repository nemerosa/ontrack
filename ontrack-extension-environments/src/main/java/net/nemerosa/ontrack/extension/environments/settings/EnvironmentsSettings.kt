package net.nemerosa.ontrack.extension.environments.settings

import net.nemerosa.ontrack.model.annotations.APIDescription

data class EnvironmentsSettings(
    @APIDescription("How the environments a build is deployed into are displayed")
    val buildDisplayOption: EnvironmentsSettingsBuildDisplayOption = EnvironmentsSettingsBuildDisplayOption.HIGHEST,
)
