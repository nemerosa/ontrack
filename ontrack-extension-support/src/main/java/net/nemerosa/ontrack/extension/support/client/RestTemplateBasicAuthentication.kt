package net.nemerosa.ontrack.extension.support.client

@Deprecated("Will be removed in V5")
data class RestTemplateBasicAuthentication(
    val username: String,
    val password: String,
): RestTemplateAuthentication
