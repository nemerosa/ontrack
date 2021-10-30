package net.nemerosa.ontrack.extension.github.ingestion.processing.model

val Repository.ontrackProjectName: String
    get() = getProjectName(owner.login, name)
