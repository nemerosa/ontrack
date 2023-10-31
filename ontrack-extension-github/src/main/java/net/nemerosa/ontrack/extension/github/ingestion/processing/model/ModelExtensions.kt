package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ValidationStamp

fun getProjectName(owner: String, repository: String, orgProjectPrefix: Boolean) =
    if (orgProjectPrefix) {
        normalizeName("$owner-$repository", Project.PROJECT_NAME_MAX_LENGTH)
    } else {
        normalizeName(repository, Project.PROJECT_NAME_MAX_LENGTH)
    }

fun normalizeName(name: String, maxLength: Int) = NameDescription.escapeName(name.lowercase()).take(maxLength)

