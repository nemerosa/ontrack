package net.nemerosa.ontrack.kdsl.spec.extension.jira

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class JiraConfiguration(
    val name: String,
    val url: String,
    val user: String = "",
    val password: String = "",
)