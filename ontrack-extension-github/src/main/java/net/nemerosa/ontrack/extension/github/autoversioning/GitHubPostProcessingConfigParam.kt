package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription

data class GitHubPostProcessingConfigParam(
    @APIDescription("Name of the parameter")
    val name: String,
    @APIDescription("Value of the parameter (can contain template expressions)")
    val value: String,
)
