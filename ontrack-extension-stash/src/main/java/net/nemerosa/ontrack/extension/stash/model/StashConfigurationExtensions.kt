package net.nemerosa.ontrack.extension.stash.model

fun getRepositoryUrl(
    configuration: StashConfiguration,
    project: String,
    repository: String
): String = if (configuration.isCloud) {
    "${configuration.url}/$project/$repository"
} else {
    "${configuration.url}/projects/$project/repos/$repository"
}