package net.nemerosa.ontrack.extensions.environments

import java.util.*

data class Environment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val order: Int,
    val description: String?,
)
