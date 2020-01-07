package net.nemerosa.ontrack.extension.scm.catalog

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class SCMCatalogEntry(
        val scm: String,
        val config: String,
        val repository: String,
        val repositoryPage: String?,
        val timestamp: LocalDateTime
) : Comparable<SCMCatalogEntry> {
    @get:JsonIgnore
    val key: String
        get() = "$scm::$config::$repository"

    override fun compareTo(other: SCMCatalogEntry): Int =
            compareValuesBy(this, other,
                    { it.scm },
                    { it.config },
                    { it.repository }
            )
}
