package net.nemerosa.ontrack.kdsl.spec.extension.jira

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.kdsl.spec.Configuration

@JsonIgnoreProperties(ignoreUnknown = true)
data class JiraConfiguration(
    override val name: String,
    val url: String,
    val user: String = "",
    val password: String = "",
) : Configuration