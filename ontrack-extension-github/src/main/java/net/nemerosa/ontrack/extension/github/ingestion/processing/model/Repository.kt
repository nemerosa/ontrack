package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    val name: String,
    val description: String?,
    val owner: Owner,
) {
    @JsonIgnore
    val fullName: String = "${owner.login}/$name"
}
