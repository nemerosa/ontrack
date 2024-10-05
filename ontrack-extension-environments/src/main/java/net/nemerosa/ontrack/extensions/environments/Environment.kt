package net.nemerosa.ontrack.extensions.environments

data class Environment(
    val id: Int,
    val name: String,
    val order: Int,
    val description: String?,
)
