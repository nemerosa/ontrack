package net.nemerosa.ontrack.extension.support.client

data class RestTemplateBasicAuthentication(
    val username: String,
    val password: String,
): RestTemplateAuthentication
