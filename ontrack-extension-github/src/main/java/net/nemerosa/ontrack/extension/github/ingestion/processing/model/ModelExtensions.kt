package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import net.nemerosa.ontrack.model.structure.NameDescription

fun getProjectName(owner: String, repository: String) =
    normalizeName("$owner-$repository")

fun normalizeName(name: String) = NameDescription.escapeName(name)