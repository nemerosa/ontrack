package net.nemerosa.ontrack.extension.stash.model

fun getRepositoryUrl(
    configuration: StashConfiguration,
    project: String,
    repository: String
): String = "${configuration.url}/projects/$project/repos/$repository"
