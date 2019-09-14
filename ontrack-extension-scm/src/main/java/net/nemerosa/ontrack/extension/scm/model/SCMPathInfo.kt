package net.nemerosa.ontrack.extension.scm.model

data class SCMPathInfo(
        val type: String,
        val url: String,
        val branch: String?,
        val commit: String?
)