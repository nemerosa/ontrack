package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import net.nemerosa.ontrack.model.structure.NameDescription

fun normalizeName(name: String) = NameDescription.escapeName(name)