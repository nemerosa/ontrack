package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.graphql.support.ListRef

data class EnvironmentFilter(
    @ListRef
    val tags: List<String> = emptyList(),
)
