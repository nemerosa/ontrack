package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.AbstractRepositoryPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository

@JsonIgnoreProperties(ignoreUnknown = true)
class PushPayload(
    repository: Repository,
    val ref: String,
    val commits: List<Commit>,
) : AbstractRepositoryPayload(
    repository,
) {
    fun isAddedOrModified(path: String): Boolean =
        isAdded(path) || isModified(path)

    fun isRemoved(path: String): Boolean =
        commits.any { it.removed.contains(path) }

    fun isModified(path: String): Boolean =
        commits.any { it.modified.contains(path) }

    fun isAdded(path: String): Boolean =
        commits.any { it.added.contains(path) }

    @JsonIgnore
    val branchName: String = ref.removePrefix("refs/heads/")

}