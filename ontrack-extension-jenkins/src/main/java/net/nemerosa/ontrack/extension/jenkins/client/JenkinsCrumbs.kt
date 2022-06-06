package net.nemerosa.ontrack.extension.jenkins.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class JenkinsCrumbs(
    val crumb: String,
    val crumbRequestField: String,
)
