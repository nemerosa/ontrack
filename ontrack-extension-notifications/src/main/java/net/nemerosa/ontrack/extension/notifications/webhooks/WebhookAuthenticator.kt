package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import java.net.http.HttpRequest

interface WebhookAuthenticator<C> {

    val type: String

    val displayName: String

    fun validateConfig(node: JsonNode): C

    fun authenticate(config: C, builder: HttpRequest.Builder)

}