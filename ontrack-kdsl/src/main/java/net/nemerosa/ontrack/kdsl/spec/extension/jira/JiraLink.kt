package net.nemerosa.ontrack.kdsl.spec.extension.jira

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class JiraLink(
    val linkName: String,
    val key: String,
)
