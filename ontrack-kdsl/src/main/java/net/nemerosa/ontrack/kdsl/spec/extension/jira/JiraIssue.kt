package net.nemerosa.ontrack.kdsl.spec.extension.jira

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class JiraIssue(
    val key: String,
    val links: List<JiraLink> = emptyList(),
)
