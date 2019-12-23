package net.nemerosa.ontrack.extension.scm.catalog

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDateTime

data class SCMCatalogEntry(
        val scm: String,
        val config: String,
        val repository: String,
        val timestamp: LocalDateTime,
        val linked: Boolean
) {
    @get:JsonIgnore
    val key: String
        get() = "$scm::$config::$repository"
}
