package net.nemerosa.ontrack.kdsl.spec.extension.jenkins

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class JenkinsConfiguration(
        val name: String,
        val url: String,
        val user: String = "",
        val password: String = "",
)