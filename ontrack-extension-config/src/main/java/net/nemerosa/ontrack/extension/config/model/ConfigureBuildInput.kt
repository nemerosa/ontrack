package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Input for the `configureBuild` mutation")
data class ConfigureBuildInput(
    @APIDescription("YAML configuration")
    val config: String,
    @APIDescription("Type of CI engine. Will be guessed from the environment if not specified.")
    val ci: String? = null,
    @APIDescription("Type of SCM. Will be guessed from the environment if not specified.")
    val scm: String? = null,
    @APIDescription("List of environment variables")
    @ListRef(embedded = true)
    val env: List<CIEnv> = emptyList(),
)