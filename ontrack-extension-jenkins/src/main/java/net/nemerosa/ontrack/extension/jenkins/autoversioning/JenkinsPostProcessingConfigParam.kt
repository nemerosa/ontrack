package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription

data class JenkinsPostProcessingConfigParam(
    @APIDescription("Name of the parameter")
    val name: String,
    @APIDescription("Value of the parameter")
    val value: String,
)
