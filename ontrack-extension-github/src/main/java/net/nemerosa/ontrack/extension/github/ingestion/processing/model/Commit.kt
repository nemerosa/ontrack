package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Commit(
    val id: String,
    val message: String,
    val author: Author?,
    val added: List<String> = emptyList(),
    val removed: List<String> = emptyList(),
    val modified: List<String> = emptyList(),
) {
    /**
     * Returns the consolidated list of all paths being impacted
     */
    fun paths(): Set<String> = added.toSet() + removed.toSet() + modified.toSet()
}
