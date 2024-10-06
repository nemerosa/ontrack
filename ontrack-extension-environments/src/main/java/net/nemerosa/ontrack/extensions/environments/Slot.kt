package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.model.structure.Project

data class Slot(
    val id: String,
    val environment: Environment,
    val description: String?,
    val project: Project,
    val qualifier: String,
) {
    companion object {
        const val DEFAULT_QUALIFIER = ""
    }
}
