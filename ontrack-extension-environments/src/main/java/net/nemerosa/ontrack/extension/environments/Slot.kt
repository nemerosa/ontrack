package net.nemerosa.ontrack.extension.environments

import net.nemerosa.ontrack.model.structure.Project
import java.util.*

data class Slot(
    val id: String = UUID.randomUUID().toString(),
    val environment: Environment,
    val description: String?,
    val project: Project,
    val qualifier: String,
) {
    companion object {
        const val DEFAULT_QUALIFIER = ""
    }

    override fun toString(): String =
        if (qualifier != DEFAULT_QUALIFIER) {
            "${environment.name}/${project.name}/${qualifier}"
        } else {
            "${environment.name}/${project.name}"
        }
}
