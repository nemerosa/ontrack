package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

data class Slot(
    val id: String,
    val environment: Environment,
    val description: String?,
    val project: Project,
    val qualifier: String?,
    val admissionRules: List<SlotAdmissionRule>,
    val deployed: Build?,
    val candidate: Build?,
)
