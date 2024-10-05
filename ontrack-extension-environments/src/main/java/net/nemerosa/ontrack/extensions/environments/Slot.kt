package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

data class Slot(
    val id: Int,
    val environment: Environment,
    val description: String?,
    val project: Project,
    val qualifier: String?,
    val deployed: Build?,
    val candidate: Build?,
)
