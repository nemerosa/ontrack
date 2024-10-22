package net.nemerosa.ontrack.extension.environments

import java.util.*

data class Environment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val order: Int,
    val description: String?,
    val tags: List<String> = emptyList(),
) {
    fun withTags(tags: List<String>) = Environment(
        id = id,
        name = name,
        order = order,
        description = description,
        tags = tags
    )
}
