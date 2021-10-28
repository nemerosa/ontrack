package net.nemerosa.ontrack.extension.github.ingestion.processing.model

val Repository.ontrackProjectName: String
    get() =
        normalizeName("${owner.login}-$name")
