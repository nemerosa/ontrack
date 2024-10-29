package net.nemerosa.ontrack.extension.environments.casc

data class EnvironmentsCascModel(
    val keepEnvironments: Boolean = true,
    val environments: List<EnvironmentCasc>,
)

data class EnvironmentCasc(
    val name: String,
    val description: String = "",
    val order: Int,
    val tags: List<String>,
)
