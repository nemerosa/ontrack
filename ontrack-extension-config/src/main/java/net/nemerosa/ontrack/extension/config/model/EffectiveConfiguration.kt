package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * CI configuration to actually use.
 */
data class EffectiveConfiguration(
    @APIDescription("Configuration for project, branch and build")
    val configuration: Configuration,
    @APIDescription("CI engine being used")
    val ciEngine: String,
    @APIDescription("SCM engine being used")
    val scmEngine: String,
)
