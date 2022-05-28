package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.extension.github.ingestion.processing.events.AbstractRepositoryPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.extension.github.ingestion.support.REFS_TAGS_PREFIX

@JsonIgnoreProperties(ignoreUnknown = true)
class PushPayload(
    repository: Repository,
    val ref: String,
    val commits: List<Commit> = emptyList(),
    @JsonProperty("head_commit")
    val headCommit: Commit? = null,
    @JsonProperty("base_ref")
    val baseRef: String? = null,
) : AbstractRepositoryPayload(
    repository,
) {

    /**
     * Checks if this push event is the creation of a tag, and if yes, returns the name of the tag.
     */
    fun getTag() = if (ref.startsWith(REFS_TAGS_PREFIX) && headCommit != null) {
        ref.removePrefix(REFS_TAGS_PREFIX)
    } else {
        null
    }

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