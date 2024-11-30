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

    fun fullName() = "${environment.name}/${project.name}${
        qualifier.takeIf { it.isNotBlank() }?.let { "/$it" } ?: ""
    }"

    override fun toString(): String = fullName()
}
