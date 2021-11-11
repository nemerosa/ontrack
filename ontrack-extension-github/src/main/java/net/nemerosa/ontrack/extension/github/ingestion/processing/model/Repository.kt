package net.nemerosa.ontrack.extension.github.ingestion.processing.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    @APIDescription("Name of the repository")
    val name: String,
    val description: String?,
    @APIDescription("Owner of the repository")
    val owner: Owner,
    @JsonProperty("html_url")
    @APIDescription("URL to the repository")
    val htmlUrl: String,
) {
    @JsonIgnore
    val fullName: String = "${owner.login}/$name"
}
