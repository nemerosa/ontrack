package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.ValidationStamp

fun getProjectName(owner: String, repository: String, orgProjectPrefix: Boolean) =
    if (orgProjectPrefix) {
        normalizeName("$owner-$repository")
    } else {
        normalizeName(repository)
    }

fun normalizeName(name: String) = NameDescription.escapeName(name.lowercase()).take(ValidationStamp.NAME_MAX_LENGTH)
