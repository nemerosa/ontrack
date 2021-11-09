package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    val name: String,
    val description: String?,
    val owner: Owner,
    @JsonProperty("html_url")
    val htmlUrl: String,
) {
    @JsonIgnore
    val fullName: String = "${owner.login}/$name"
}
